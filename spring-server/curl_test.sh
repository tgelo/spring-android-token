#Authenticate
TOKEN=$(curl -H "Content-Type: application/json" -X POST -d '{"username":"admin","password":"admin"}' http://localhost:8080/api/authenticate)
echo "-----"
echo "Authentication token: " ${TOKEN}
echo "-----"
# Get user

USER=$(curl -H "x-auth-token":${TOKEN} -X GET http://localhost:8080/api/user)
echo "-----"
echo "User: " ${USER}
echo "-----"
echo