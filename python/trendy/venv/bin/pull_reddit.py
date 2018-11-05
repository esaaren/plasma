import time
import praw
import json
import psycopg2
from TrendyHelper import TrendyHelper
import nltk
from nltk.sentiment.vader import SentimentIntensityAnalyzer
from nltk.sentiment.vader import SentiText
from nltk import tokenize
nltk.download('vader_lexicon')

def run():
    start = time.time()
    analyzer = SentimentIntensityAnalyzer()

    trendy = TrendyHelper('reddit')

    reddit = trendy.get_reddit()

    # Subreddit name
    subreddit = reddit.subreddit('all')
    comments = subreddit.stream.comments()
    escape_limit = 100000
    x = 0
    trendy.open_conn()
    for comment in comments:
        if comment.ups > 0:
            vs = analyzer.polarity_scores(comment.body)
            sentiment = vs['compound']
            data = ['reddit', comment.id, comment.permalink, comment.body, sentiment]
            trendy.insert_trendybase(data)
        x = x + 1
        if x > escape_limit:
            break
    trendy.close_conn()

if __name__ == '__main__':
    run()



"""
If you use the VADER sentiment analysis tools, please cite:

Hutto, C.J. & Gilbert, E.E. (2014). VADER: A Parsimonious Rule-based Model for
Sentiment Analysis of Social Media Text. Eighth International Conference on
Weblogs and Social Media (ICWSM-14). Ann Arbor, MI, June 2014.
"""