import pandas as pd
from pathlib import Path
from argparse import ArgumentParser
from typing import List
import json



def parse_args():
    p = ArgumentParser(description='Returns nodes and edges on the email graph')

    p.add_argument('PATH', type=Path,
                   help='Path toe the `email headers.csv` file.')
    p.add_argument('--out', type=Path,
                   help='Output nodes and edges JSON to a file')

    return p.parse_args()


def process_to_col(text: str) -> list:
    """Process the `To` column into a list of recipients.

    Remove quotation marks, spaces, lower, and split into a list
    """
    return text \
        .strip('"') \
        .strip(" ") \
        .lower() \
        .split(",")


def get_all_users(df: pd.DataFrame) -> list:
    """Gets all users involved in email exchanges."""
    users = set()
    for user in df['From'].tolist():
        users.add(user.lower())

    for recipients in df['To'].tolist():
        for recipient in process_to_col(recipients):
            users.add(recipient)

    return list(users)


def generate_edges(df: pd.DataFrame) -> List[dict]:
    """Generates a list of dictionary entries.

    Returns:
        List of dictionaries. Each dictionary contains: [`edge_id`, `subject`,
        `date`, `from`, `to`]
    """
    result = []
    dfd = df.to_dict('split')  # [D]ata[F]rame as [D]ictionary
    curr_id = 0
    for i in range(len(df)):
        data = dfd['data']
        f = data[i][0].lower()  # f since from is a reserved keyword
        to = process_to_col(data[i][1])
        date = data[i][2]
        subject = data[i][3]
        for recipient in to:
            edge = {'edge_id': curr_id,
                    'subject': subject,
                    'date': str(date),
                    'from': f,
                    'to': recipient}
            curr_id += 1
            result.append(edge)

    return result


if __name__ == '__main__':
    args = parse_args()

    headers = pd.read_csv(args.PATH, parse_dates=['Date'])

    output = {'nodes': get_all_users(headers),
              'edges': generate_edges(headers)}

    if args.out is not None:
        with open(args.out, 'w') as out_file:
            json.dump(output, out_file)
    else:
        print(json.dumps(output))
