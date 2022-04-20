"""Article Embedding.

Produces a TSNE embedding of every article.
"""
from argparse import ArgumentParser
from pathlib import Path
import json
from typing import List, Union
from datetime import datetime

import numpy as np
import pandas as pd
from sklearn.manifold import TSNE
from sklearn.feature_extraction.text import TfidfVectorizer
import plotly.express as px


def parse_args():
    p = ArgumentParser(description='Produces an embedding based on article '
                                   'contents')

    p.add_argument('PATH', type=Path,
                   help='Path to the `article_list.json` file.')
    p.add_argument('--out', type=Path,
                   help='Output nodes and edges JSON to a file')
    p.add_argument('--start', type=str,
                   help='Start of date range, inclusive')
    p.add_argument('--end', type=str,
                   help='End of date range, inclusive')

    return p.parse_args()


def read_articles(fp: Path, date_range: Union[List[str], None] = None
                  ) -> pd.DataFrame:
    """Reads all given articles and turns them into a table."""
    articles = {'fileName': [],
                'publication': [],
                'pubId': [],
                'title': [],
                'date': [],
                'content': []}

    pub_ids = {}

    # Parse date_range if it is given
    if date_range is not None:
        start_date = datetime.strptime(date_range[0], '%Y-%m-%d')
        end_date = datetime.strptime(date_range[1], "%Y-%m-%d")

    with open(fp) as file:
        for line in file:
            data = json.loads(line)
            curr_line = {}
            curr_line_in_date_range = True
            for key in articles.keys():
                try:
                    if key == 'content':
                        content = data[key] \
                            .strip() \
                            .removesuffix(']') \
                            .removeprefix('[')
                        curr_line[key] = content
                    elif key == 'publication':
                        if data[key] not in pub_ids:
                            # Add to pub_ids
                            pub_ids[data[key]] = len(pub_ids)  # Count as ID
                        curr_line[key] = data[key]
                        curr_line['pubId'] = pub_ids[data[key]]
                    elif key == 'pubId':
                        # Already handled in key == 'publication'
                        pass
                    elif key == 'title':
                        title = data[key].strip()
                        if len(title) == 0:
                            title = 'Title Not Given'
                        curr_line[key] = title
                    elif key == 'date':
                        # Remove the time
                        formatted_date = ','.join(data[key].split(',')[:2])
                        dt_date = datetime.strptime(formatted_date,
                                                    "%b %d, %Y")
                        if date_range is not None:
                            if start_date <= dt_date <= end_date:
                                pass
                            else:
                                curr_line_in_date_range = False
                        curr_line[key] = formatted_date
                    else:
                        curr_line[key] = data[key]
                except KeyError:
                    if key == 'title':
                        value = 'Title Not Given'
                    else:
                        value = 'Unknown'
                    curr_line[key] = value
            if curr_line_in_date_range:
                for k, v in curr_line.items():
                    articles[k].append(v)

    return pd.DataFrame.from_dict(articles)


def prepare_embeddings(embedded: np.ndarray,
                       articles: pd.DataFrame) -> pd.DataFrame:
    """Prepares embeddings by adding data to them for visualization."""
    embedded = pd.DataFrame(embedded)
    output = articles.drop('content', axis=1)
    output['x'] = embedded[0].tolist()
    output['y'] = embedded[1].tolist()

    return output


def main():
    args = parse_args()

    if args.start is not None and args.end is not None:
        date_range = [args.start, args.end]
    else:
        date_range = None

    articles = read_articles(args.PATH, date_range)

    vectorized = TfidfVectorizer().fit_transform(articles['content'].tolist())
    embedded = TSNE(learning_rate='auto', random_state=0,
                    verbose=0, n_jobs=-1,
                    perplexity=10.).fit_transform(vectorized)
    embedded = prepare_embeddings(embedded, articles)

    if args.out is not None:
        fig = px.scatter(embedded, x='x', y='y', color='publication',
                         hover_name='title', hover_data=['date'])
        fig.show()
        print(f"Writing out TSNE coordinates to {args.out}...")
        embedded.to_csv(args.out, index=False)
    else:
        print(embedded.to_csv(index=False))



if __name__ == '__main__':
    main()
