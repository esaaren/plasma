from TrendyHelper import TrendyHelper
import psycopg2

def run():

    trendy = TrendyHelper('reddit')
    trendy.open_conn()
    data = ["reddit", "123", "www.test.com", "asadsadasdasda", 0.767687]
    trendy.insert_trendybase(data)
    trendy.close_conn()


if __name__ == '__main__':
    run()