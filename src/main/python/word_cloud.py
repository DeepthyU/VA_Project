import sys
import matplotlib.pyplot as plt
import json
from wordcloud import WordCloud, STOPWORDS
import calendar
import time
import gensim

arg = sys.argv[1]
score = 0
if len(sys.argv) > 2:
    score = sys.argv[2]

f = open(arg, "r")
text = f.read()


all_stopwords = gensim.parsing.preprocessing.STOPWORDS
local_stopwords = set()
local_stopwords.update(STOPWORDS)
local_stopwords.update(all_stopwords)
custom_stopwords = {"year", "time", "said", "years", "new", "day", "question", "including","causes","days","remember", "denied","previous","soon","fields","reports", "change", "think", "aforesaid", "ask", "past", "current", "today", "field", "old", "hour", "place", "way", "local", "update", "bring", "site", "area", "come", "center", "break", "cause", "event", "know", "asked", "end", "started", "continue", "person", "need", "high", "caused", "dob", "left", "answer", "window", "times", "leave", "concerning", "help", "answered", "thing", "situation", "point", "sent", "morning", "driver", "life", "problem", "yesterday", "wfa", "resource", "action", "mean", "truck", "lead", "told", "good", "bend", "increase", "recent", "month", "taken", "known", "level", "hours", "outside", "questions", "little", "means", "took", "calls", "girl", "near", "second", "man", "began", "right", "door", "says", "large", "brought", "called", "possible", "certain", "groups", "additional", "present", "moved", "given", "clear", "jan", "ago", "use", "gen", "far", "updates", "turned", "long", "production", "responsible", "history", "offshore", "buildings", "lack", "held", "comment", "provide", "curve"}
local_stopwords.update(custom_stopwords)
# Generate word cloud
wordcloud = WordCloud(width= 1000, height = 600, normalize_plurals=False, include_numbers=False, collocation_threshold=30, random_state=1, background_color='white', colormap='viridis', collocations=False, min_word_length = 3, stopwords = local_stopwords).generate(text)
    

gmt = time.gmtime()
ts = calendar.timegm(gmt)
filename = "article"+str(ts)
    
#Save image
wordcloud.to_file(filename+".png")

print(filename)

