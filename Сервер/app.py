from datetime import timedelta
from flask import Flask
from flask_jwt_extended import JWTManager
from flask_migrate import Migrate
from flask_restful import Api, Resource, reqparse, abort
from flask_sqlalchemy import SQLAlchemy

from models import db_sessions
from models.token import RevokedTokenModel
from resurces import author_resurce, blogs_resurce , comment_resurce

app = Flask(__name__)
app.config['SECRET_KEY'] = 'kondrashen_secret_key'
app.config['JWT_SECRET_KEY'] = 'jwt-secret-kondrashin'
app.config["JWT_ACCESS_TOKEN_EXPIRES"] = timedelta(hours=1)
app.config["JWT_REFRESH_TOKEN_EXPIRES"] = timedelta(days=1)
app.config['JWT_BLACKLIST_ENABLED'] = True
app.config['JWT_BLACKLIST_TOKEN_CHECKS'] = ['access', 'refresh']
conn_str = db_sessions.global_init()
app.config['SQLALCHEMY_DATABASE_URI'] = conn_str
db = SQLAlchemy(app)

migrate = Migrate(app, db_sessions)
api = Api(app, catch_all_404s=True)

jwt = JWTManager(app)

api.add_resource(author_resurce.AuthorRegistration, '/registration')
api.add_resource(author_resurce.AuthorLogin, '/login')
api.add_resource(author_resurce.AuthorLogoutAccess, '/logout/access')
api.add_resource(author_resurce.AuthorLogoutRefresh, '/logout/refresh')
api.add_resource(author_resurce.TokenRefresh, '/token/refresh')
api.add_resource(author_resurce.AllAuthors, '/authors')
api.add_resource(author_resurce.SecretResource, '/secret')
api.add_resource(author_resurce.AuthorResource, '/author/<author_id>')

api.add_resource(blogs_resurce.BlogListResource, '/api/blogs')
api.add_resource(comment_resurce.CommentListResource, '/api/blogs/<blog_id>/comments')
api.add_resource(comment_resurce.CommentResource, '/api/comments/<comm_id>')
api.add_resource(blogs_resurce.BlogResource, '/api/blogs/<blog_id>')




@jwt.token_in_blocklist_loader
def check_if_token_is_revoked(jwt_header, jwt_payload):
    jti = jwt_payload["jti"]
    token = RevokedTokenModel.is_jti_blacklisted(jti)
    return token is not None


@app.route('/')
def index():
    return "Ну работай....!"


def main():
    app.run(host='0.0.0.0', port=5000)


if __name__ == '__main__':
    main()
