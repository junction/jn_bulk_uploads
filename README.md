Junction Networks Bulk Uploader -
===========================================================================================
Leverages web services to import users, phones and external resources in batch

IMPORTANT: In order to build the Bulk Uploader on a Mac USING ANT, the requirements are Ant,
JarBundler (http://informagen.com/JarBundler/) and PackageMaker which installs with XCode.

1. Author
---------------------------------------------------------------------------------------------
Oren Forer
oren@junctionnetworks.com

Date Created:
10-27-2010

Modified:
11-22-2013


2. Development
----------------------------------------------------------------------------------------------
In order to setup the development enviroment the following resources need to be configured.
a. The source code - src/
b. The resources (property files, log configurations,  and images) - src/resources
c. lib dependencies - lib/

Additionally, to setup the testing environment.
a. Requires junit
b. The test cases - test/
c. The resources used in the test cases  - test/resources


3. Installation
-----------------------------------------------------------------------------------------------
There are three steps necessary to complete the install.
a. Run the "Junction Networks Bulk Uploader" package installer

4. Fixes / Enhancements
---------------------------------------------------------------------------------------------

12/20/2010
- Added support for batch uploading or importing Phones, specifically Polycom phones.
- Wrote an Ant based build utility.

11/22/2013
- User field - new field Send Welcome Email
- Phone field - new field to set NatKeepAlive.  Also set the default password to
  organization password
- Bulk import Support for External SIP Address
- Bulk import support for External Telephone Number
- Codebase refactor

5. Packaging (requires PackageMaker and JarBundler)
---------------------------------------------------------------------------------------------
- Type "ant" to see the help
- Executing "Ant compile" will compile all the source, create the Jar files,
  run the Jar Bundler utility, and initiate PackageMaker.
- In order to Run "Ant compile", Ant PackageMaker,
  and "JarBundler.jar" (http://informagen.com/JarBundler/) are required.
- Create the app bundle using "Jar Bundler" followed by "PackageMaker".
  PackageMaker comes with XCode.

6. Using
---------------------------------------------------------------------------------------------
The Bulk Uploader comes in a Mac Package Installer.  Running the installer
will place the app "Junction Networks Bulk Uploader.app" into Mac Applications.
Running the app requires a number of fields to be entered.
I.   OnSIP username
II.  OnSIP password
III. OnSIP domain
IV.  File Selection of a CSV file.

There are 4 optional upload formats.  The first will upload User data, and the second will
upload Phone data, followed by the External SIP Addresses and External Telephone numbers.

a. Users example:
> First, Last, Extension, Email, Voicemail(Y/N), Send Welcome Email (Y/N)
> test_123, test_565, 2121, cust_1234@example.com, Y, Y

- The first line is assumed to be the header and is required.  The script will start
  reading data from the second line of the CSV file.
- First Name, Last Name, Extension, Email, Include Voicemail (Y, N), Send Welcome Email (Y,N)

b. Phones example:
> MacAddress, Make, Model, GMT Offset, NatKeepalive (Y/N), SetOrganizationWebPassword (Y/N),Company Directory (Y/N)
> 0004f2accaca, Polycom, Polycom Soundpoint 330, 4, Y, N, Y

- The first line is assumed to be the header and is required for phone imports.
- Valid GMT Offsets are (-12, -11.5, -11, -10.5, -10 ... , 0, ... .5, 1, 1.5, 2, ... 13)
- MacAddress can be found on the back of the phone.

c. External SIP Addresses:
> External SIP Address, Name, Extension
> bob@example.onsip.com, Bob, 2343

- The external sip address domain cannot be the same as the admin's domain

d. External Telephone Number:
> External Telephone Number
> 1-332-333-2322
