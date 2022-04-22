import sys
import matplotlib.pyplot as plt
import json
from wordcloud import WordCloud, STOPWORDS
import calendar
import time
import gensim

#print("Hello Baeldung Readers!!")
#print(f"Name of the script      : {sys.argv[0]=}")
#print(f"Arguments of the script : {sys.argv[1:]=}")	

arg = sys.argv[1]
#print(f"Arguments:", arg)
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
wordcloud = WordCloud(normalize_plurals=False, include_numbers=False, collocation_threshold=30, width = 3000, height = 2000, random_state=1, background_color='black', colormap='Set2', collocations=False, min_word_length = 3, stopwords = local_stopwords).generate(text)
    
#wordcloud = WordCloud(width = 3000, height = 2000, random_state=1, background_color='black', colormap='Set2', collocations=False, stopwords = STOPWORDS).generate(text)
# Plot
# Set figure size
#plt.figure(figsize=(40, 30))
# Display image
#plt.imshow(wordcloud) 
# No axis details
#plt.axis("off");


gmt = time.gmtime()
ts = calendar.timegm(gmt)
filename = "article"+str(ts)
    
# Save image
# wordcloud.to_file(filename+".png")
dict = wordcloud.words_
#word_list=list(dict.keys())
words_list = []
for key in dict:
    if float(dict[key]) >= float(score):
	    words_list.append(key)
with open(filename+".txt", "w") as outfile:
		outfile.write("##".join(words_list))

print(filename)

