import datetime
import uuid

import sqlalchemy
from sqlalchemy import orm
from sqlalchemy.dialects.postgresql import UUID
from models import db_sessions
from sqlalchemy_serializer import SerializerMixin


from .db_sessions import SqlAlchemyBase


class Blog(SqlAlchemyBase, SerializerMixin):
    __tablename__ = 'blogs'

    id = sqlalchemy.Column(sqlalchemy.Integer, primary_key=True, autoincrement=True)
    # id = sqlalchemy.Column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    title = sqlalchemy.Column(sqlalchemy.String, nullable=True)
    date = sqlalchemy.Column(sqlalchemy.DateTime, default=datetime.datetime.now)
    content = sqlalchemy.Column(sqlalchemy.String, nullable=True)


    # is_published = sqlalchemy.Column(sqlalchemy.Boolean, default=True)
    authorId = sqlalchemy.Column(sqlalchemy.Integer, sqlalchemy.ForeignKey('authors.id', ondelete='CASCADE'))
    author = orm.relationship('Author')

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

