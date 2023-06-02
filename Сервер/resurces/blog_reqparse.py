from flask_restful import reqparse

parser = reqparse.RequestParser()
parser.add_argument('id')
parser.add_argument('title', help='This field cannot be blank', required=True)
parser.add_argument('date')
parser.add_argument('content', help='This field cannot be blank', required=True)
parser.add_argument('authorId')
