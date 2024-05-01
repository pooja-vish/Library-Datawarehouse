#!/usr/bin/env python
# coding: utf-8

# In[47]:


import csv
def insert_data_into_sourcedb(target_db, csv_file, table_name) :
    connection=pymysql.connect(host='localhost', user='root', password='Feb@231997', db=target_db)
    cursor=connection.cursor()
    try:
        if(table_name=="books"):
            insert_sql= """
            INSERT INTO books (bid, bName, bAuthor, publishYear, noOfCopies)
            VALUES (%s, %s, %s, %s, %s);
            """
        elif(table_name=="borrowers"):
            insert_sql= """
            INSERT INTO borrowers (mid, bid, bDate, transType)
            VALUES (%s, %s, %s, %s);
            """
        elif(table_name=="members" and target_db=="windsor"):
            insert_sql= """
            INSERT INTO members (mid, mName, mEmail, mPhoneNo)
            VALUES (%s, %s, %s, %s);
            """
        elif(table_name=="members" and target_db=="toronto"):
            insert_sql= """
            INSERT INTO members (mid, mName, mPhone, mAddress)
            VALUES (%s, %s, %s, %s);
            """
        with open(csv_file, 'r') as file:
            csv_reader = csv.reader(file)
            next(csv_reader)  
            for row in csv_reader:
                print(row)
                cursor.execute(insert_sql, row)
        connection.commit()
        print("Data inserted successfully in table : ", table_name)
    except pymysql.Error as e:
        connection.rollback()
        print("Error connecting to db ", e)
    finally:
        connection.close()
        cursor.close()
        
def main():
    # Fetch data from source table
    db=("windsor","toronto")
    tables=("books", "members", "borrowers")
    csv_files=("books_windsor", "members_windsor", "borrowers_windsor","books_toronto", "members_toronto", "borrowers_toronto")
    k=0;
    for i in db:
        for j in tables:
            path=f"C:\\Users\\dellf\\University\\2nd Sem\\Emerging Non Traditional DB\\dataset\\{csv_files[k]}.csv"
            k=k+1;
            insert_data_into_sourcedb(i, path, j)

if __name__ == "__main__":
    main()



# In[ ]:





# In[ ]:




