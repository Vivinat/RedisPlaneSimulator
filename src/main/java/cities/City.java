package cities;

import planes.Plane;

import java.time.LocalDateTime;
import java.util.*;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

public class City {

    public String name;
    public List<Plane> planesOnCity = Collections.synchronizedList(new ArrayList<>());;
    private int planeCapacity;
    public String commandTowerChannelId;
    private Deque<String> inWaitingQueue = new ArrayDeque<>();
    public int routeId;
    private static final String REDIS_HOST = "127.0.0.1";
    private static final int REDIS_PORT = 6379;

    public City(String name, int planeCapacity, int routeId) {
        this.name = name;
        this.planeCapacity = planeCapacity;
        this.commandTowerChannelId = UUID.randomUUID().toString().substring(0, 10);
        this.routeId = routeId;
        new Thread(this::listenToCommandTower).start();
    }

    public void publishToCommandTower(String message) {
        try (Jedis jedis = new Jedis(REDIS_HOST, REDIS_PORT)) {
            jedis.publish(commandTowerChannelId, message);
            System.out.println("Command Tower in city " + name + " published on channel " + commandTowerChannelId + ": " + message);
        }
    }

    private void listenToCommandTower() {
        new Thread(() -> {
            try (Jedis jedis = new Jedis(REDIS_HOST, REDIS_PORT)) {
                jedis.subscribe(new CommandTowerListener(), commandTowerChannelId);
            }
        }).start();
    }

    public class CommandTowerListener extends JedisPubSub {
        @Override
        public void onMessage(String channel, String message) {
            if (message.contains("Authorized-") || message.contains("Unauthorized-")){
                return;
            }
            String[] parts = message.split("-");
            String planeId = parts[1];

            System.out.println("\nCommand Tower in city " + name + " received message from plane " + planeId + ": " + message);

            if (AnalyseCapacity()) {
                publishToCommandTower("Authorized-" + planeId);
                try (Jedis jedis = new Jedis(REDIS_HOST, REDIS_PORT)) {
                    jedis.sadd(name + ":authorized_planes", planeId);
                }
            } else {
                if (!inWaitingQueue.contains(planeId)) {
                    publishToCommandTower("Unauthorized-" + planeId);
                    inWaitingQueue.push(planeId);
                }
            }
        }
    }

    public void NotifyLeavingAirport() {
        if (planesOnCity.size() < planeCapacity && !inWaitingQueue.isEmpty()) {
            String planeToEnterId = inWaitingQueue.pop();
            publishToCommandTower("Authorized-" + planeToEnterId);
        }
    }

    private boolean AnalyseCapacity() {
        return planesOnCity.size() < planeCapacity;
    }

    public boolean retrievePlaneAuthorization(String planeId) {
        try (Jedis jedis = new Jedis(REDIS_HOST, REDIS_PORT)) {
            return jedis.sismember(name + ":authorized_planes", planeId);
        }
    }
}

