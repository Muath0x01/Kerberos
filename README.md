# Kerberos
A kerberos implementation in Java

## What is Kerberos?
```
Kerberos is an authentication protocol, and at the same time a KDC, that has become very popular. Originally designed at MIT, it is 
named after the three-headed dog in Greek mythology that guards the gates of Hades. Kerberos has gone through several versions. The 
general concept is shown in Figure 1 and the detailed interaction is shown in Figure 2. This Kerberos works as a Key Distribution Center
(KDC). It provides authentication, key generation, and key distribution.
```

![Figure1](https://github.com/Muath0x01/Kerberos/blob/master/1.PNG)
![Figure2](https://github.com/Muath0x01/Kerberos/blob/master/2.PNG)

## Defenetions

```
Authentication Server (AS): The authentication server (AS) is the KDC in the Kerberos protocol. Each user registers with the AS and is granted a user identity and a password. The AS has a database with these identities and the corresponding passwords. The AS verifies the use, issues a session key to be used between Alice and the TGS, and sends a ticket for the TGS.
Ticket-Granting Server (TGS): The ticket-granting server (TGS) issues a ticket for the real server (Bob). It also provides the session key (KAB) between Alice and Bob. Kerberos has separated user verification from the issuing of tickets. In this way, though Alice verifies her ID just once with AS, she can contact the TGS multiple times to obtain tickets for different real servers.
Real Server: The real server (Bob) provides services for the user (Alice). Kerberos is designed for a client-server program, in which a user uses the client process to access the server. Kerberos is not used for person-to-person authentication.
```
## Operation
```
A client process (Alice) can access a process running on the real server (Bob) in six steps as shown in Figure 2.
1. Alice sends her request to the AS in plain text using her registered identity.
2. The AS sends a message encrypted with Alice’s permanent symmetric key, KA-AS. The message contains two items: a session key, KA-TGS, that is used by Alice to contact the TGS, and a ticket for the TGS that is encrypted with the TGS symmetric key, KAS-TGS. Alice does not know KA-AS, but when a message arrives, she types her symmetric password. The password and the appropriate algorithm together create KA-AS if the password is correct. The password is then immediately destroyed; it is not send to the network and it does not stay in the terminal. It is used only for a moment to create KA-AS. The process now uses KA-AS to decrypt the message sent. KA-TGS and the ticket are extracted.
3. Alice now sends three items to the TGS. The first is the ticket received from the AS. The second is the name of the real server (Bob), and the third is a timestamp that is encrypted by KA-TGS. The timestamp prevents a replay by Eve.
4. Now the TGS sends two tickets, each containing the session key between Alice and Bob, KA-B. The ticket for Alice is encrypted with KA-TGS; the ticket for Bob is encrypted with Bob’s Key, KTGS-B. Note that Eve cannot extract KAB because Eve does not know KA-TGS or KTGS-B. She cannot replace the timestamp with a new one (she does not know KA-TGS). Even if she is very quick and sends the step 3 message before the timestamp has expired, she still receives the same two tickets that she cannot decipher.
5. Alice sends Bob’s ticket with timestamp encrypted by KA-B.
6. Bob confirms the receipt by adding 1 to the timestamp. The message is encrypted with KA-B and sent to Alice.
```

### Interaction Entities

```
* AS server implemented by the multithreaded class AS and accepts users’ requests on port X. It also has a method createAccount that can be used to add a user ID and password to the User-AS password table.
* TGS server implemented by the multithreaded class TGS and accepts users’ requests on port Y. It also has a method createAccount that can be used to add a server ID and password to the Server-TGS password table.
* Server (Bob) implemented by the multithreaded class Server and accepts users’ requests on port Z1 a variable port number entered by the user to run multiple Servers each on different port.
* Client (Alice) implemented by the class User. You can run multiple copies of this class to interact with different Servers. The User class gets the ID name of the Server (Bob or other servers) to interact with. It will need later on the get the user to enter the password. This class also prints the timestamp sent to the server and the received timestamped from the Server. You can use a randomly generated number as a timestamp.
```

### Prerequisites

```
JAVA 8 
```

**Notes:**
* This project depends on external libraies for string manipulation, the library is commons-lang3-3.5-bin.tar which will be included and needs to be added to the project in eclipse.

* AS-TGS Password stored in AS and in TGS: KerberosIsTheBest999
