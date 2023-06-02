from flask_restful import reqparse

parserlog = reqparse.RequestParser()
parserlog.add_argument('email', help='This field cannot be blank', required=True)
parserlog.add_argument('password', help='This field cannot be blank', required=True)