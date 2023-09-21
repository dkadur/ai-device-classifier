import sqlite3

conn = sqlite3.connect('image_database.db')
c = conn.cursor()

c.execute('CREATE TABLE IF NOT EXISTS images (path TEXT)')

conn.commit()
conn.close()