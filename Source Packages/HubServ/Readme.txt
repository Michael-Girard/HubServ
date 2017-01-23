HubServlet.java is where the behind-the-scenes logic happens. When the user clicks a button to submit, the code being run is determined by one or more switches. One is for the page the submission originates from and the other is, for example, "selection" from adminmain.jsp, which can result in displaying different pages from the administrator's main menu.

The login method checks the validity of the username. Next, it validates the password by hashing the entered password and compares it with a hashed password in a ResultSet (obtained from the getUserInfo method). If it's a match, it checks to see if the person is a technician or admin and sets a session attribute accordingly.

getUserInfo queries the database for a user's information based on the username supplied at the login screen. It returns a ResultSet object.

addUser lets administrators add a new person to the database so new technicians or administrators can be added. The password is hashed before being stored.

getStudentInfo looks up student personal information based on their 900 number and populates the documentation.jsp page with the resulting information. It's also used when submitting 

addStudent adds a new student's personal information to the access database if it doesn't already exist.

isDataValid performs a series of checks on documentation when being submitted to ensure all required fields are filled out and no fields have obviously erroneous data.

addVisit adds a new visit for the student. There's one of each student. Each student can have multiple visits, and each visit can have multiple devices. So two levels of one-to-many relationships exist in the database.

addServices adds each new device that was serviced to that visit.

setDocAttributes is the method that gives the documentation page "memory". Before, if you typed anything into the fields and changed the number of devices to 2, the page would have to be regenerated and all the data would be lost. Now, all the data is stored and the setDocAttributes method restores all the data that would have been lost.

createDocumentation is the method that handles the dynamic generation of all of the content for the documentation.jsp page.
