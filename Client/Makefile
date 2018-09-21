all: java.policy compile-client

java.policy: FORCE
	@echo "Creating client java policy"
	@echo "grant codebase \"file:`pwd`/\" {" > java.policy
	@echo "permission java.security.AllPermission;" >> java.policy
	@echo "};" >> java.policy

FORCE: ;

../Server/RMIInterface.jar: ../Server/Server/Interface/IResourceManager.java
	make -C ../Server/ RMIInterface.jar

../Server/MethodLambda.jar: ../Server/Server/Common/MethodLambda.java
	make -C ../Server/ MethodLambda.jar

compile-client: ../Server/RMIInterface.jar ../Server/MethodLambda.jar
	javac -cp ../Server/RMIInterface.jar:../Server/MethodLambda.jar Client/*.java

clean:
	rm -f Client/*.class
	rm -f ../Server/RMIInterface.jar
	rm -f ../Server/MethodLambda.jar
	rm -f java.policy
