import datetime
import uuid

import sqlalchemy
# from app import db
# from flask_login import UserMixin
from sqlalchemy import orm

from sqlalchemy_serializer import SerializerMixin
from werkzeug.security import generate_password_hash, check_password_hash

from passlib.hash import pbkdf2_sha256 as sha256

from models import db_sessions

from .db_sessions import SqlAlchemyBase



class Author(SqlAlchemyBase, SerializerMixin):
    __tablename__ = 'authors'

    id = sqlalchemy.Column(sqlalchemy.Integer, primary_key=True, autoincrement=True)
    firstname = sqlalchemy.Column(sqlalchemy.String, nullable=True)
    lastname = sqlalchemy.Column(sqlalchemy.String, nullable=True)
    phone = sqlalchemy.Column(sqlalchemy.BIGINT, nullable=True)
    email = sqlalchemy.Column(sqlalchemy.String, index=True, unique=True)
    password = sqlalchemy.Column(sqlalchemy.String, index=True)
    registration_date = sqlalchemy.Column(sqlalchemy.DateTime, default=datetime.datetime.now)

    def __repr__(self):
        return f'<Author> {self.id} {self.firstname} {self.lastname} {self.email}'

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

    @classmethod
    def find_by_email(cls, email):
        session = db_sessions.create_session()
        return session.query(Author).filter(Author.email == email).first()

    @staticmethod
    def return_all():
        def to_json(x):
            return {
                'email': x.email,
                'password': x.password
            }

        session = db_sessions.create_session()
        return {'authors': list(map(lambda x: to_json(x), session.query(Author).all()))}

    @staticmethod
    def generate_hash(password):
        return sha256.hash(password)

    @staticmethod
    def verify_hash(password, hash):
        return sha256.verify(password, hash)


