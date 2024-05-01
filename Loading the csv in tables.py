#!/usr/bin/env python
# coding: utf-8

# In[ ]:


#install pymysql first using pip install and then import for connecting to datawarehouse
import pymysql
#import csv
import csv
#add your hostname, username, password and database name
conn = pymysql.connect(host='localhost',

                       user='root',

                       password='password',

                       db='windsor')
cur = conn.cursor()
#open the csv file from which you want to load data
file = open('C:\\Users\\dellf\\University\\2nd Sem\\Emerging Non Traditional DB\\borrower.csv')
type(file)
csvreader = csv.reader(file)
header = []
header = next(csvreader)
#for each row in csv file add the row in table
for row in csvreader:
    cur.execute('INSERT INTO borrowers (borrower_id, member_id, transaction_date, transaction_type) VALUES (%s, %s, %s, %s)', row)
#close the connection to the database.
conn.commit()
#close the connection
cur.close()

