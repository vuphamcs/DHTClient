DHTClient
=========

This is a DHTClient that can construct PING, GETSUCCESSOR, PUT, and GET messages to send to peers, and receive their responses. 
Usage:

javac DHTClient.java
java DHTClient "host IP" "host port" "peer IP" "peer port" "message type" "key/value"

The 6th argument is only used when making a GET request.
