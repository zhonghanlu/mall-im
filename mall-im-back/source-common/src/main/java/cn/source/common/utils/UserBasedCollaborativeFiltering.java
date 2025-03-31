package cn.source.common.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserBasedCollaborativeFiltering {

    // 计算两个用户的余弦相似度
    public static double cosineSimilarity(Map<Long, Long> user1, Map<Long, Long> user2) {
        double dotProduct = 0.0;
        double normUser1 = 0.0;
        double normUser2 = 0.0;

        for (Long item : user1.keySet()) {
            if (user2.containsKey(item)) {
                dotProduct += user1.get(item) * user2.get(item);
            }
            normUser1 += Math.pow(user1.get(item), 2);
        }

        for (double value : user2.values()) {
            normUser2 += Math.pow(value, 2);
        }

        return dotProduct / (Math.sqrt(normUser1) * Math.sqrt(normUser2));
    }

    // 找到相似用户并推荐物品
    public static List<Long> recommendItems(Map<Long, Map<Long, Long>> userRatings, Long targetUser) {
        Map<Long, Long> targetRatings = userRatings.get(targetUser);
        Map<Long, Double> similarityScores = new HashMap<>();

        // 计算与其他用户的相似度
        for (Long user : userRatings.keySet()) {
            if (!user.equals(targetUser)) {
                double similarity = cosineSimilarity(targetRatings, userRatings.get(user));
                similarityScores.put(user, similarity);
            }
        }

        // 根据相似度推荐物品
        List<Long> recommendations = new ArrayList<>();
        for (Map.Entry<Long, Double> entry : similarityScores.entrySet()) {
            Long similarUser = entry.getKey();
            for (Long item : userRatings.get(similarUser).keySet()) {
                if (!targetRatings.containsKey(item) && !recommendations.contains(item)) {
                    recommendations.add(item);
                }
            }
        }
        return recommendations;
    }

    public static void main(String[] args) {
        // 用户对物品的评分数据
        Map<Long, Map<Long, Long>> userRatings = new HashMap<>();
        userRatings.put(11L, new HashMap<Long, Long>() {{
            put(1L, 5L);
            put(2L, 3L);
            put(3L, 4L);
        }});

        userRatings.put(22L, new HashMap<Long, Long>() {{
            put(1L, 4L);
            put(2L, 3L);
            put(4L, 2L);
        }});

        userRatings.put(33L, new HashMap<Long, Long>() {{
            put(2L, 4L);
            put(3L, 2L);
            put(4L, 5L);
        }});

        List<Long> recommendations = recommendItems(userRatings, 11L);
        System.out.println("Recommended items for user1: " + recommendations);
    }
}
