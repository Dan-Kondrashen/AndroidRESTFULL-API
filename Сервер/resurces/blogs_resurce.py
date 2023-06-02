import uuid

from flask import jsonify
from flask_restful import abort, Resource

from models import db_sessions
from models.blogs import Blog
from resurces.blog_reqparse import parser


def abort_if_blog_not_found(blog, id):
    if not blog:
        abort(404, message=f"Blog with number {id} not found")


def find_blog(blog_id):
    session = db_sessions.create_session()
    blog = session.query(Blog).get(blog_id)
    return blog


class BlogResource(Resource):

    def get(self, blog_id):

        session = db_sessions.create_session()
        blog = session.query(Blog).get(blog_id)
        abort_if_blog_not_found(blog, blog_id)
        return jsonify(blog.to_dict(only=('id', 'title', 'date', 'content', 'authorId')))

    def put(self, blog_id):
        args = parser.parse_args()
        blog = find_blog(blog_id)
        abort_if_blog_not_found(blog, blog_id)
        if (args['content'] != ""):
            blog.title = args['title']
            blog.date = args['date']
            blog.content = args['content']
            blog.update_to_db()
            return jsonify(status='Успех!')
        else:
            return jsonify(status='Поле контента блога не может быть пустым!')

    def delete(self, blog_id):
        session = db_sessions.create_session()
        session.query(Blog).filter(Blog.id==blog_id).delete()
        session.commit()
        return jsonify({'success': 'OK'})




class BlogListResource(Resource):
    def get(self):
        session = db_sessions.create_session()
        blogs = session.query(Blog).all()
        return jsonify([item.to_dict(only=('id', 'title', 'date', 'content', 'authorId')) for item in blogs])

    def post(self):
        args = parser.parse_args()
        blog = Blog(**args)
        blog.save_to_db()
        return jsonify({'success': 'OK'})
