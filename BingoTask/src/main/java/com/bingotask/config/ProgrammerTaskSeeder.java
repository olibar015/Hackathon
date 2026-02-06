package com.bingotask.config;

import com.bingotask.model.Task;
import com.bingotask.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class ProgrammerTaskSeeder implements CommandLineRunner {

  @Autowired
  private TaskRepository taskRepository;

  @Override
  public void run(String... args) throws Exception {
    if (taskRepository.count() == 0) {
      List<Task> programmerTasks = Arrays.asList(
        // Core Programming Tasks
        createTask("Complete Code Review", "Review 2 pull requests and provide feedback", 50, "coding", "/icons/code-review.png"),
        createTask("Write Unit Tests", "Write tests for a new feature", 45, "testing", "/icons/test.png"),
        createTask("Debug Production Issue", "Investigate and fix a production bug", 70, "coding", "/icons/debug.png"),
        createTask("Refactor Code", "Refactor a messy module", 55, "coding", "/icons/refactor.png"),
        createTask("Document API", "Write OpenAPI/Swagger documentation", 50, "documentation", "/icons/api-docs.png"),

        // DevOps & Infrastructure
        createTask("Setup CI/CD", "Configure CI/CD pipeline", 80, "devops", "/icons/cicd.png"),
        createTask("Optimize Database", "Optimize slow database queries", 60, "database", "/icons/database.png"),
        createTask("Security Audit", "Perform security vulnerability check", 75, "security", "/icons/security.png"),
        createTask("Performance Tuning", "Optimize application performance", 65, "performance", "/icons/performance.png"),

        // Learning & Development
        createTask("Solve Coding Problems", "Solve 3 problems on LeetCode", 50, "learning", "/icons/leetcode.png"),
        createTask("Learn New Framework", "Spend 1 hour learning a new framework", 55, "learning", "/icons/learn.png"),
        createTask("Read Tech Article", "Read 2 technical articles", 35, "learning", "/icons/article.png"),
        createTask("Watch Conference Talk", "Watch a tech conference recording", 40, "learning", "/icons/conference.png"),

        // Health & Wellness
        createTask("20-20-20 Eye Rule", "Follow eye strain prevention rule", 30, "health", "/icons/eyes.png"),
        createTask("Ergonomic Stretch", "15-minute programmer stretch routine", 35, "health", "/icons/stretch.png"),
        createTask("Walk Break", "Take a 10-minute walk break", 25, "health", "/icons/walk.png"),
        createTask("Stay Hydrated", "Drink 8 glasses of water", 20, "health", "/icons/water.png"),

        // Collaboration
        createTask("Pair Programming", "30 minutes of pair programming", 45, "collaboration", "/icons/pair.png"),
        createTask("Mentor Junior Dev", "Help a junior developer", 50, "collaboration", "/icons/mentor.png"),
        createTask("Team Code Review", "Participate in team code review", 40, "collaboration", "/icons/team-review.png"),

        // Productivity
        createTask("Deep Work Session", "2 hours of focused coding", 60, "productivity", "/icons/deepwork.png"),
        createTask("Plan Sprint Tasks", "Plan development tasks for next sprint", 45, "productivity", "/icons/plan.png"),
        createTask("Clean Workspace", "Organize digital and physical workspace", 30, "productivity", "/icons/clean.png"),
        createTask("Backup Projects", "Backup important code and projects", 35, "productivity", "/icons/backup.png"),

        // Community
        createTask("Open Source Contribution", "Contribute to open source project", 75, "community", "/icons/opensource.png"),
        createTask("Write Blog Post", "Share knowledge through blogging", 55, "community", "/icons/blog.png"),
        createTask("Answer Stack Overflow", "Help others on Stack Overflow", 50, "community", "/icons/stackoverflow.png")
      );

      taskRepository.saveAll(programmerTasks);
      System.out.println("Programmer tasks seeded: " + programmerTasks.size());
    }
  }

  private Task createTask(String title, String description, int points, String category, String iconUrl) {
    Task task = new Task();
    task.setTitle(title);
    task.setDescription(description);
    task.setPoints(points);
    task.setCategory(category);
    task.setIconUrl(iconUrl);
    task.setIsActive(true);
    return task;
  }
}
