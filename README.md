Junction Networks Bulk Uploader -
--------------------------------------------------------
Leverages web services to import users & phones in batch

IMPORTANT: In order to build the Bulk Uploader on a Mac USING ANT, both Ant and JarBundler (http://informagen.com/JarBundler/) are required.

1. Author
--------------
Oren Forer
oren@junctionnetworks.com

Date Created:
10-27-2010

Modified:
11-22-2013

2. Installation
-------------------
There are three steps necessary to complete the install.
a. Run the "Junction Networks Bulk Uploader" package installer
b  Due to an SSL verification issue of the jnctn.com domain you'll need to execute a manual command on your MAC using the java keytool utility.
You MAY need to Copy the content below into a file called 'www.jnctn.com' (the name actually doesn't matter)
from and including
-----BEGIN CERTIFICATE-----  to and including -----END CERTIFICATE-----

-----BEGIN CERTIFICATE-----
MIIFjzCCBHegAwIBAgIQJPrXTaJ+UUY7Tuw2pU2FFDANBgkqhkiG9w0BAQUFADBi
MQswCQYDVQQGEwJVUzEhMB8GA1UEChMYTmV0d29yayBTb2x1dGlvbnMgTC5MLkMu
MTAwLgYDVQQDEydOZXR3b3JrIFNvbHV0aW9ucyBDZXJ0aWZpY2F0ZSBBdXRob3Jp
dHkwHhcNMTAwOTI3MDAwMDAwWhcNMTQxMjA2MjM1OTU5WjCBpzELMAkGA1UEBhMC
VVMxDjAMBgNVBBETBTE4OTQwMQswCQYDVQQIEwJQQTEQMA4GA1UEBxMHTmV3dG93
bjEbMBkGA1UECRMSMjg2NSBTLiBFYWdsZSBSb2FkMRowGAYDVQQKExFKdW5jdGlv
biBOZXR3b3JrczEYMBYGA1UECxMPU2VjdXJlIExpbmsgU1NMMRYwFAYDVQQDEw13
d3cuam5jdG4uY29tMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2FRG
Y55c5rmtyOnQk6h5r3qQNKYNvVZ3p/DLcR4Y0fPimoXvOeeY5P9SUSmMZC6y1Od9
53Je4HOnuBmiX8jDWOANv6bhvEJPEAhzpo8ksCpKJLzcDkRsfHBOrlZZ+mhSjqZX
fSn3mw5hE5HtVdof0x6Re/ZhvN6agrihAwbUpL445jWw+gxnu0ttRZ/Tn4bWvT42
YxFn6U8f/01mUBZtt9D3cc+Sq+EiFDSV5J/x3b+x8KYmbIeWfrhvU+tp8SmVFjeu
Q7UJp2SjJFD+e9hF1619BxdpcGsfaUhRlLY6c7cyqyj6rIvJDdYuuCGPt5zRAL4J
j/+tKTBTQO7t5+4ySQIDAQABo4IB+TCCAfUwHwYDVR0jBBgwFoAUPEHijwgIqUwl
iY1txTjQ/IWMYhcwHQYDVR0OBBYEFASL3rRrq1iQOz7z6mh98gOFdspQMA4GA1Ud
DwEB/wQEAwIFoDAMBgNVHRMBAf8EAjAAMB0GA1UdJQQWMBQGCCsGAQUFBwMBBggr
BgEFBQcDAjBrBgNVHSAEZDBiMGAGDCsGAQQBhg4BAgEDATBQME4GCCsGAQUFBwIB
FkJodHRwOi8vd3d3Lm5ldHdvcmtzb2x1dGlvbnMuY29tL2xlZ2FsL1NTTC1sZWdh
bC1yZXBvc2l0b3J5LWNwcy5qc3AwegYDVR0fBHMwcTA2oDSgMoYwaHR0cDovL2Ny
bC5uZXRzb2xzc2wuY29tL05ldHdvcmtTb2x1dGlvbnNfQ0EuY3JsMDegNaAzhjFo
dHRwOi8vY3JsMi5uZXRzb2xzc2wuY29tL05ldHdvcmtTb2x1dGlvbnNfQ0EuY3Js
MHMGCCsGAQUFBwEBBGcwZTA8BggrBgEFBQcwAoYwaHR0cDovL3d3dy5uZXRzb2xz
c2wuY29tL05ldHdvcmtTb2x1dGlvbnNfQ0EuY3J0MCUGCCsGAQUFBzABhhlodHRw
Oi8vb2NzcC5uZXRzb2xzc2wuY29tMBgGA1UdEQQRMA+CDXd3dy5qbmN0bi5jb20w
DQYJKoZIhvcNAQEFBQADggEBAC8dGuVvue69vDzIsxwacmOHXts/tkdPdf2T/b2K
p36OFjWyUyzUJnRmiOBflmLDvpYYy3PZGXC9k4k7eoew3Ye+0IZGycHrelRpkdEm
DEgpQisYFJgbz195L8C6AXstIktrugNOjGrX+XA0MEb1EuWB8ES4Xf/w3w1cBlps
jr0jOosxTNRGs1VufNgiLl1tSNX3PzqoSdQ/BM+A1Xiz9Lpb0VfMf6Gc6cJLe6TD
xvxrOi+dPpAImpuZAAnkyoSsJCoKjKWoESL3ivhg4GZrvBcFV7mpeGw4kNngLIqy
gBz0H5Lf2LvbRnZ67jsB+Ogv8GR+o1kdRwKunOiMnY1AiYI=
-----END CERTIFICATE-----

c. Due to SSL verification issues the follow command needs to be executed as root.
      sudo keytool -import -trustcacerts \
        	-file <path-to-cert-file>/cert/www.jnctn.com \
		-alias www.jnctn.com \
		-keystore /System/Library/Frameworks/JavaVM.framework/Versions/1.6.0/Home/lib/security/cacerts

      - Enter your root password to execute keytool
      - When prompted for the "keystore password", the default pwd is "changeit"


3. Development
--------------------
In order to setup the development enviroment the following resources need to be configured.
a. The source code - src/
b. The resources (property files, log configurations,  and images) - src/resources
c. lib dependencies - lib/

Additionally, to setup the testing environment.
a. Requires junit
b. The test cases - test/
c. The resources used in the test cases  - test/resources

4. Git Repository
---------------------
git@github.com:junction/jn_bulk_uploads.git

5. Fixes / Enhancements
------------------------

12/20/2010
- Added support for batch uploading or importing Phones, specifically Polycom phones.
- Wrote an Ant based build utility.

11/22/2013
- User field - new field Send Welcome Email
- Phone field - new fields Set NatKeepAlive, Set the defaul password to organization password
- Bulk import Support for External SIP Address
- Bulk import support for External Telephone Number
- Codebase refactor

6. Packaging
-----------------------
- Created an Ant based build file. Executing "Ant compile" will compile all the source, create the Jar files,
  run the Jar Bundler utility, and initiate PackageMaker.
- In order to Run "Ant compile" Both Ant and "JarBundler.jar" (http://informagen.com/JarBundler/) are required.
- Created the app bundle using "Jar Bundler" followed by "PackageMaker". These come with XCode.

8. Using
------------------------
Please Review #2 (Installation) Of this README file.  The Bulk Uploader will install properly but will fail
to run if #2 (Installation) isn't executed.  The Bulk Uploader comes in a Mac Package Installer.  Running the
installer will drop the "Junction Networks Bulk Uploader.app" into Mac Applications.  Running the app
requires a number of fields to be entered.
I.   OnSIP username
II.  OnSIP password
III. OnSIP domain
IV.  File Selection of a CSV file.

There are 4 optional formats.  The first will upload User data, and the second will upload Phone data,
followed by External SIP Addresses and External Telephone numbers.

a. Users example:
******************************************************************************************************************
First,Last,Extension,Email,Voicemail(Y/N),Send Welcome Email (Y/N)
test_123,test_565,2121,cust_1234@example.com,Y,Y
******************************************************************************************************************
The first line is assumed to be the header and is required.  The script will start reading data from the second
line of the CSV file.
First Name, Last Name, Extension, Email, Include Voicemail (Y, N)

b. Phones example:
******************************************************************************************************************
MacAddress, Make (all polycom for now), Model, GMT Offset, NatKeepalive (N/Y), SetOrganizationWebPassword (N/Y)
0004f2accaca, Polycom, Polycom Soundpoint 330, 4, Y, N
******************************************************************************************************************
The first line is assumed to be the header and is required for phone imports.
Valid GMT Offsets are (-12, -11.5, -11, -10.5, -10 ... , 0, ... .5, 1, 1.5, 2, ... 13)
MacAddress can be found on the back of the phone.

c. External SIP Addresses:
******************************************************************************************************************
External SIP Address, Username
bob@example.onsip.com, Bob
******************************************************************************************************************
- The external sip address domain cannot be the same as the admin's domain


d. External Telephone Number:
******************************************************************************************************************
External Telephone Number
1-332-333-2322
******************************************************************************************************************


9. BUGS
--------------------------
developer@junctionnetworks.com



