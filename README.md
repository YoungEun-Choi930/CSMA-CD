# CSMA-CD
데이터 통신 과제 충돌감지 알고리즘 구현


- Environment
  + JAVA IDE : IntelliJ 2021.2.3
  + 언어 : JAVA
  + 서버 : AWS EC2 Ubuntu 
 
 

- Files
  + Server    
 서버 소켓을 생성한 후, 연결되면 소켓을 받아와서 LinkThread에 소켓을 넘겨준다. LinkThread는 4개를 생성한다. 

  + LinkThread   
 클라이언트의 소켓에서 받은 수신자와 송신자에게 링크 사용 가능 여부를 알려준다. 

  + Client   
는 노드를 4개를 생성하고 시작시키며 타이머를 측정한다. 시간이 60초가 되면 노드를 종료시킨다.

  + Node   
 서버 소켓과 연결되면 SendThread와 이너클래스인 ReceiveThread를 실행시킨다.

  + ReceiveThread   
소켓에서 받은 데이터를 이용하여 노드파일에 결과를 작성한다.    
실패하였다면 BackoffTimer 시간 후에 SendThread를 호출한다. 성공하였다면 임의의 시간 후에 SendThread를 호출한다.

  + SendThread   
 수신자를 결정하여 소켓에 보낸다.
 
 ---------------------
 
- Usage   
  1. AWS ubuntu에 JAVA다운로드.   
  2. AWS에서 Server클래스 실행.       
  3. IntelliJ에서 Client클래스 실행.   
output : Link, node1, node2, node3, node4 .txt file
      
        
 - Additional Comments
    - 오버헤드 처리   
프로그램 실행 시 링크 사용 시간은 5msec이지만, 앞뒤로 코드를 처리하는데에 있어서 오버헤드가 발생하기 때문에 정확하게 5msec로  작동될 수 없다.    
특히나 첫 번째 전송시간에서는 오버헤드가 크게 발생한다.  따라서 링크에서 Accept을 받고 Finish되는데 걸리는 시간을 5msec로 작성하지 않고 오버헤드가 포함된 시간으로 작성하였다.   
   
    - 스레드 병렬성 처리   
어떤 스레드가 먼저 파일에 접근하느냐에 따라 파일에 write 되는 시간이 차이가 난다. 따라서 파일에 쓸 때 순서를 정렬해주기 위해 WriteLink 클래스의 우선순위큐를 이용하였다.   
   
    - 서버시간과 클라이언트의 시간 차 문제   
서버의 시작시간과 노드들의 시작시간이 차이가 나서 파일에 적히는 시간은 동일하게 찍히지 않는다. 하지만 처리하는 시간을 계산해보았을 때는 동일한 시간이 걸리고 순서도 알맞다.   
