#### Taskmixer

A simple solution to dispatch command strings over a local network using RabbitMQ.

In taskmixer we have a command producer (see `taskmixer.producer` project) that simply appends a string command to a RabbitMQ queue, and one or more command consumers (see `taskmixer.consumer` project) that wait for commands and execute them upon receipt. In the following we will call *task* a command string such as `ls -l`.

Producer and consumers may be on the same or on different machines.

RabbitMQ ensures that tasks are not lost even if consumers die or the machine containing the RabbitMQ server is rebooted.

Note that taskmixer does not handle the sharing of data but just the dispatch of tasks.
To share data you might use NFS.

#### How to build the applications
```
git clone https://github.com/acco93/taskmixer.git

cd taskmixer

cd taskmixer.core
gradle build
cd ../

cd taskmixer.producer
gradle build
cd ../

cd taskmixer.consumer
gradle build
cd ../

mkdir -p build
cp taskmixer.producer/build/libs/txp-1.0.jar build/txp.jar
cp taskmixer.consumer/build/libs/txc-1.0.jar build/txc.jar
```

Now the directory `build` contains the task producer application `txp.jar` and the task consumer application `txc.jar`

#### Setup RabbitMQ

##### Producer side
Install a RabbitMQ server

```
sudo apt install rabbitmq-server
```

You might need to create an account
```
sudo rabbitmqctl add_user username password
sudo rabbitmqctl set_user_tags username administrator
sudo rabbitmqctl set_permissions -p / username ".*" ".*" ".*"
```
Check https://www.rabbitmq.com/rabbitmqctl.8.html#User_Management for more details.

Now you can send tasks to the queue from any machine in the local network by running

```
java -jar txp.jar 'ls -l' -u username -p password -i 192.168.xxx.xxx
```

The above line sends the task `ls -l` to a RabbitMQ server located at IP `192.168.xxx.xxx`

By specifying the `-w` flag, the producer can wait for results printed to the standard output due to the command, if any.

```
java -jar txp-1.0.jar 'i=0; while [ $i -lt 10 ]; do echo $i; sleep 1; i=$[$i+1]; done' -u username -p password -i 192.168.xxx.xxx -w
```

By using the `-b` flag, the command is broadcast to all available consumers.
```
java -jar txp-1.0.jar 'ls -l' -u username -p password -i 192.168.xxx.xxx -b
```

Note that `-w` and `-b` together are currently not supported.

##### Consumer side
We can wait for tasks by using

```
java -jar txc.jar -u username -p password -i 192.168.xxx.xxx
```
Consumers process one task at a time. RabbitMQ dispatches tasks to the first available consumer by following a round robin strategy.

#### Convert to eclipse project
The repository can be easily converted into an eclipse project by using gradle
```
cd taskmixer
cd taskmixer.core/ && gradle eclipse && cd ..
cd taskmixer.producer/ && gradle eclipse && cd ..
cd taskmixer.consumer/ && gradle eclipse && cd ..
```
After that, go to eclipse File -> Import -> General -> Existing Projects into Workspace -> Select root directory browse selecting the git repository
