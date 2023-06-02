import datetime

from flask import jsonify
from flask_jwt_extended import (create_access_token, create_refresh_token, jwt_required, get_jwt_identity, get_jwt)
from flask_restful import abort, Resource

from models import db_sessions

from models.authors import Author
from models.token import RevokedTokenModel
from resurces.author_reqparse import parser
from resurces.author_login_reqparse import parserlog
from resurces.author_update_reqparse import parserup


def abort_if_author_not_found(author, id):
    if not author:
        abort(404, message=f"Author with number {id} not found")
def revoked_token(jti):
    now = datetime.datetime.now(datetime.timezone.utc)
    token = RevokedTokenModel(jti=jti, created_date=now)
    token.save_to_db()

class AuthorRegistration(Resource):
    def post(self):
        data = parser.parse_args()
        if Author.find_by_email(data['email']):
            return jsonify(status='Пользователь с таким email уже существует')
        else:
            new_author = Author(
                firstname=data['firstname'],
                lastname=data['lastname'],
                email=data['email'],
                password=Author.generate_hash(data['password'])
            )

            try:
                new_author.save_to_db()
                access_token = create_access_token(identity=new_author.id)
                refresh_token = create_refresh_token(identity=new_author.id)
                return jsonify(
                    message='User {} was created'.format(data['email']),
                    refreshToken=refresh_token,
                    accessToken=access_token
                )
            except:
                return jsonify(status='Вы зарегистрированы!')


class AuthorLogin(Resource):
    def post(self):
        data = parserlog.parse_args()
        current_author = Author.find_by_email(data['email'])
        if not current_author:
            return jsonify(status='Пользователь не найден!')

        if Author.verify_hash(data['password'], current_author.password):
            # time_delta = datetime.timedelta(minutes=20)
            # author_dict = current_author.to_dict(only=('id', 'firstname', 'email'))
            # access_token = create_access_token(identity=author_dict,
            #                                    fresh=time_delta)  # , expires_delta=time_delta
            # refresh_token = create_refresh_token(identity=author_dict)
            return jsonify(status='Вы успешно авторизованы!', authId=current_author.id)
        # refreshToken = refresh_token,
        # accessToken = access_token
        else:
            return jsonify(status='Неверный пароль!!!')


class AuthorLogoutAccess(Resource):
    @jwt_required(fresh=True)
    def post(self):
        jti = get_jwt()['jti']
        try:
            revoked_token(jti)
            return jsonify(
                message='Access token has been revoked')
        except:
            return jsonify(
                message='Wrong credentials')


class AuthorLogoutRefresh(Resource):
    def post(self):
        return {'message': 'Author logout'}


class TokenRefresh(Resource):
    @jwt_required(refresh=True)
    def post(self):
        current_user = get_jwt_identity()
        time_delta = datetime.timedelta(minutes=15)
        access_token = create_access_token(identity=current_user, fresh=time_delta)
        return jsonify(access_token=access_token)


class AllAuthors(Resource):

    def get(self):
        session = db_sessions.create_session()
        authors = session.query(Author).all()
        return jsonify([item.to_dict(only=('id', 'firstname', 'lastname', 'phone', 'email')) for item in authors])

    def delete(self):
        return Author.delete_all()


class AuthorResource(Resource):

    def get(self, author_id):
        session = db_sessions.create_session()
        author = session.query(Author).get(author_id)
        abort_if_author_not_found(author, author_id)
        return jsonify(author.to_dict(only=('id', 'firstname', 'lastname', 'phone', 'email')))

    def put(self, author_id):
        args = parserup.parse_args()
        session = db_sessions.create_session()
        author = session.query(Author).get(author_id)
        abort_if_author_not_found(author, author_id)
        if Author.find_by_email(args['email']):
            author.firstname = args['firstname']
            author.lastname = args['lastname']

            author.phone = args['phone']
            author.update_to_db()
            return jsonify(status='Пользователь обновлен успешно! Однако автор с таким email уже существует, поэтому email остался прежним!')
        else:
            author.firstname = args['firstname']
            author.lastname = args['lastname']
            author.email = args['email']
            author.phone = args['phone']
            author.update_to_db()
            return jsonify(status='Пользователь обновлен успешно!')

    def delete(self, author_id):
        session = db_sessions.create_session()
        session.query(Author).filter(Author.id == author_id).delete()
        session.commit()
        return jsonify({'success': 'OK'})



class SecretResource(Resource):
    @jwt_required(fresh=True)
    def get(self):
        return jsonify(
            answer=42
        )