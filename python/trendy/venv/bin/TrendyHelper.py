import psycopg2
import praw
import logging


logging.basicConfig()
logger = logging.getLogger('TRENDY_HELPER')
logger.setLevel(logging.INFO)

class TrendyHelper():
    def __init__(self, datasource):
        self.datasource = datasource
        self.insert_statement = """insert into trendy_data values (%s, %s, %s, %s, %s);"""
        self.url = '35.203.53.31'
        self.database = 'postgres'
        self.user = 'postgres'
        self.password = 'trendyadmin'

    def insert_trendybase(self,  data):
        logger.info('Inserting comment id: {} '.format(data[1]))
        try:
            conn = self.trendybase_conn
            cursor = conn.cursor()
            cursor.execute(self.insert_statement, data)
            conn.commit()
            cursor.close()
        except Exception as e:
            logger.error('Failed to insert comment id: {}'.format(data[1]))
            logger.error(e)

        return 0

    def get_reddit(self):
        reddit = praw.Reddit(client_id='gluFwvMrQLqLuA',
                             client_secret='nowLOmNuC8tS76mrc-LQUlarngw',
                             user_agent='testscript by /u/plasmatrendybot',
                             password='plasmafury10',
                             username='plasmatrendybot')
        return reddit


    def open_conn(self):
        try:
            self.trendybase_conn = psycopg2.connect(
                                host=self.url,
                                database=self.database,
                                user=self.user,
                                password=self.password)
            logger.info('Connection to Trendybase is open...')
        except:
            logger.error('Connection to Trendybase failsed!')
            return -1
        return 0


    def close_conn(self):
        logger.info('Closing Trendybase connection')
        try:
            self.trendybase_conn.close()
        except:
            logger.error('Closing Trendybase connection failed!')
            return -1
        return 0