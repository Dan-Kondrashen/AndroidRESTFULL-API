import datetime
import uuid

import sqlalchemy
from sqlalchemy import orm
from sqlalchemy.dialects.postgresql import UUID
from models import db_sessions
from sqlalchemy_serializer import SerializerMixin

from .db_sessions import SqlAlchemyBase


class Comment(SqlAlchemyBase, SerializerMixin):
    __tablename__ = 'comments'

    id = sqlalchemy.Column(sqlalchemy.Integer, primary_key=True, autoincrement=True)
    content = sqlalchemy.Column(sqlalchemy.String, nullable=True)
    comment_date = sqlalchemy.Column(sqlalchemy.DateTime, default=datetime.datetime.now)
    authorId = sqlalchemy.Column(sqlalchemy.Integer, sqlalchemy.ForeignKey('authors.id', ondelete='CASCADE'))
    blogId = sqlalchemy.Column(sqlalchemy.Integer, sqlalchemy.ForeignKey('blogs.id', ondelete='CASCADE'))
    author = orm.relationship('Author')
    blog = orm.relationship('Blog')

    def save_to_db(self):
        session = db_sessions.create_session()
        session.add(self)
        session.commit()

    def update_to_db(self):
        session = db_sessions.create_session()
        session.merge(self)
        session.commit()

    def delete_from_db(self):
        session = db_sessions.create_session()
        session.delete(self)
        session.commit()
