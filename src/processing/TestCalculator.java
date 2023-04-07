package processing;

import java.awt.*;
import java.util.*;

public class TestCalculator {
    public static Map<Point, Set<Point>> connectPoints(Set<Point> startPoints, Set<Point> targetPoints, int numConnectionsPerTarget, int maxConnectionsPerStart) {
        Map<Point, Set<Point>> connections = new HashMap<>();
        Map<Point, Set<Point>> reverseConnections = new HashMap<>();

        // initialize connections with random start-target pairings
        for (Point start : startPoints) {
            ArrayList<Point> targetList = new ArrayList<>(targetPoints);
            Collections.shuffle(targetList);
            int numConnections = 0;
            Set<Point> connectedTargets = new HashSet<>();
            for (Point target : targetList) {
                if (numConnections < maxConnectionsPerStart && !connectedTargets.contains(target) && (!connections.containsKey(target) || connections.get(target).size() < numConnectionsPerTarget)) {
                    if (!connections.containsKey(target)) {
                        connections.put(target, new HashSet<>());
                    }
                    connections.get(target).add(start);
                    if (!reverseConnections.containsKey(start)) {
                        reverseConnections.put(start, new HashSet<>());
                    }
                    reverseConnections.get(start).add(target);
                    connectedTargets.add(target);
                    numConnections++;
                }
            }
        }

        // optimize connections
        boolean improved = true;
        while (improved) {
            improved = false;
            for (Point target : targetPoints) {
                if (connections.containsKey(target) && connections.get(target).size() > numConnectionsPerTarget) {
                    Set<Point> candidates = new HashSet<>(connections.get(target));
                    while (connections.get(target).size() > numConnectionsPerTarget && !candidates.isEmpty()) {
                        Point candidate = null;
                        double minDistance = Double.MAX_VALUE;
                        for (Point c : candidates) {
                            double distance = 0.0;
                            int count = 0;
                            for (Point p : connections.get(target)) {
                                if (!p.equals(c)) {
                                    distance += target.distance(p);
                                    count++;
                                }
                            }
                            distance /= count;
                            if (distance < minDistance) {
                                candidate = c;
                                minDistance = distance;
                            }
                        }
                        if (candidate != null) {
                            connections.get(target).remove(candidate);
                            reverseConnections.get(candidate).remove(target);
                            candidates.remove(candidate);
                            improved = true;
                        }
                    }
                }
            }
            for (Point start : startPoints) {
                if (reverseConnections.containsKey(start) && reverseConnections.get(start).size() > maxConnectionsPerStart) {
                    Set<Point> candidates = new HashSet<>(reverseConnections.get(start));
                    while (reverseConnections.get(start).size() > maxConnectionsPerStart && !candidates.isEmpty()) {
                        Point candidate = null;
                        double minDistance = Double.MAX_VALUE;
                        for (Point c : candidates) {
                            double distance = 0.0;
                            int count = 0;
                            for (Point p : reverseConnections.get(start)) {
                                if (!p.equals(c)) {
                                    distance += start.distance(p);
                                    count++;
                                }
                            }
                            distance /= count;
                            if (distance < minDistance) {
                                candidate = c;
                                minDistance = distance;
                            }
                        }
                        if (candidate != null) {
                            reverseConnections.get(start).remove(candidate);
                            connections.get(candidate).remove(start);
                            candidates.remove(candidate);
                            improved = true;
                        }
                    }
                }
            }
        }
        // return final connections
        for (Point target : targetPoints) {
            while (connections.containsKey(target) && connections.get(target).size() > numConnectionsPerTarget) {
                Point candidate = connections.get(target).iterator().next();
                connections.get(target).remove(candidate);
                reverseConnections.get(candidate).remove(target);
            }
        }
        for (Point start : startPoints) {
            while (reverseConnections.containsKey(start) && reverseConnections.get(start).size() > maxConnectionsPerStart) {
                Point candidate = reverseConnections.get(start).iterator().next();
                reverseConnections.get(start).remove(candidate);
                connections.get(candidate).remove(start);
            }
        }
        for (Point target : targetPoints) {
            if (!connections.containsKey(target)) {
                connections.put(target, new HashSet<>());
            }
        }
        for (Point start : startPoints) {
            if (!reverseConnections.containsKey(start)) {
                reverseConnections.put(start, new HashSet<>());
            }
        }
        for (Point target : targetPoints) {
            while (connections.get(target).size() < numConnectionsPerTarget) {
                double minDistance = Double.MAX_VALUE;
                Point candidate = null;
                for (Point start : startPoints) {
                    if (!connections.containsKey(target) || !connections.get(target).contains(start)) {
                        double distance = target.distance(start);
                        if (distance < minDistance) {
                            candidate = start;
                            minDistance = distance;
                        }
                    }
                }
                if (candidate != null) {
                    connections.get(target).add(candidate);
                    reverseConnections.get(candidate).add(target);
                }
            }
        }
        return connections;
    }
}
