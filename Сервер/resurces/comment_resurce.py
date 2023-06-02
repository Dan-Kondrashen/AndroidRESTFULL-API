import uuid

from flask import jsonify
from flask_restful import abort, Resource

from models import db_sessions
from models.comments import Comment
from resurces.comment_reqparse import parser


def abort_if_comment_not_found(comm, id):
    if not comm:
        abort(404, message=f"Comment with number {id} not found")


def find_comment(comm_id):
    session = db_sessions.create_session()
    comment = session.query(Comment).get(comm_id)
    return comment


class CommentResource(Resource):

    def get(self, comm_id):
        comm_uuid = uuid.UUID(comm_id)
        comm = find_comment(comm_uuid)
        abort_if_comment_not_found(comm, comm_id)
        return jsonify(comm.to_dict(only=('id', 'content', 'comment_date', 'authorId', 'blogId')))

    def put(self, comm_id):
        args = parser.parse_args()
        comm = find_comment(comm_id)
        abort_if_comment_not_found(comm, comm_id)
        if(args['content']==""):
            return jsonify(status="Комментарий не может быть пустым")
        else:
            comm.content = args['content']
            comm.comment_date = args['comment_date']
            comm.authorId = args['authorId']
            comm.blogId = args['blogId']
            comm.update_to_db()
            return jsonify(status="Успешно")

    def delete(self, comm_id):
        session = db_sessions.create_session()
        session.query(Comment).filter(Comment.id == comm_id).delete()
        session.commit()
        return jsonify({'success': 'OK'})


class CommentListResource(Resource):
    def get(self, blog_id):
        session = db_sessions.create_session()
        comments = session.query(Comment).filter_by(blogId=blog_id)
        return jsonify([item.to_dict(only=('id', 'content', 'comment_date', 'authorId', 'blogId')) for item in comments])

    def post(self, blog_id):
        args = parser.parse_args()
        comm = Comment(**args)
        comm.blogId = blog_id
        comm.save_to_db()
        return jsonify({'success': 'OK'})
