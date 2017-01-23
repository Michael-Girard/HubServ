This directory contains all of the JSP files. These are the web pages that are sent by the servlet to anybody using the system.

Users start out at the login screen, where they'll type in their username and password. The usernames and passwords are stored in a Microsoft Access database in a hashed form. The password that the user types in is hashed and compared with the hash stored in the database. If it's a match, the user is accepted. The program then looks at a boolean value in the database associated with that account to see if the user is a technician or an administrator.

If technician, the user will see the documentation button only (main.jsp). If administrator, other functions will appear, such as the ability to add additional users to the database, letting them sign in (adminmain.jsp).

------------

The documentation screen is as follows:

At the top, there's a text box for number of devices. Typing in a new number will change the layout of the documentation and allow the user to enter data for multiple devices simultaneously, since some clients bring in multiple devices. While the interface can handle any number of devices, I've capped it at 9. Additional coding ensures that the information in the other text boxes isn't lost if the number of devices is changed and the page must be regenerated after the name, email, etc. are entered.

Next, there are spaces for the student's personal information. When entering the student's unique 900 number, the lookup button can be clicked to check for existing entries in the database, which, if found, will populate the rest of the student's personal information automatically.

For each device, there is a separate section. Nothing of particular note here.

All fields in the documentation are checked for sanity (900 number are only 9 digits and the user must have at least 1 device, for example) and completeness (most fields are required)

Upon submission, the information in the documentation screen is entered into the access database and the success.jsp screen is displayed for a few seconds. Afterwards, it goes back to the main menu.
