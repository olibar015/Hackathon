USE bingodb;

-- Insert sample tasks
INSERT IGNORE INTO tasks (title, description, points, category, icon_url) VALUES
('Complete 5 exercises', 'Do 5 different exercises', 50, 'exercise', '/icons/exercise.png'),
('Walk 10k steps', 'Walk 10,000 steps today', 50, 'exercise', '/icons/walk.png'),
('Stretch routine', 'Complete a 15-minute stretching routine', 30, 'exercise', '/icons/stretch.png'),
('Read for 30 min', 'Read a book for 30 minutes', 40, 'reading', '/icons/reading.png'),
('Learn new skill', 'Spend 1 hour learning something new', 60, 'learning', '/icons/learn.png'),
('Meditate 10 min', 'Meditate for 10 minutes', 30, 'meditation', '/icons/meditate.png'),
('Practice gratitude', 'Write down 3 things you''re grateful for', 30, 'mindfulness', '/icons/gratitude.png'),
('Journal 15 min', 'Journal for 15 minutes', 35, 'mindfulness', '/icons/journal.png'),
('Drink 8 glasses', 'Drink 8 glasses of water today', 20, 'health', '/icons/water.png'),
('Healthy breakfast', 'Eat a healthy breakfast', 25, 'health', '/icons/food.png'),
('No caffeine PM', 'No caffeine after 2 PM', 30, 'health', '/icons/caffeine.png'),
('Early to bed', 'Go to bed before 10 PM', 40, 'health', '/icons/sleep.png'),
('Call a friend', 'Call or video chat with a friend', 35, 'social', '/icons/call.png'),
('Help someone', 'Help someone with a task', 50, 'social', '/icons/help.png'),
('Compliment 3 people', 'Give genuine compliments to 3 people', 35, 'social', '/icons/compliment.png'),
('Family time', 'Spend quality time with family', 45, 'social', '/icons/family.png'),
('Cook a meal', 'Cook a meal from scratch', 45, 'productivity', '/icons/cook.png'),
('Clean workspace', 'Organize and clean your workspace', 25, 'productivity', '/icons/clean.png'),
('Organize files', 'Organize digital files', 30, 'productivity', '/icons/organize.png'),
('Review goals', 'Review and update your goals', 35, 'productivity', '/icons/goals.png'),
('Plan tomorrow', 'Plan your schedule for tomorrow', 25, 'productivity', '/icons/plan.png'),
('Deep work 2hrs', '2 hours of focused deep work', 60, 'productivity', '/icons/deepwork.png'),
('No social media', 'No social media for 24 hours', 40, 'digital', '/icons/nosocial.png'),
('Take breaks', 'Take regular breaks every hour', 20, 'digital', '/icons/breaks.png'),
('Practice hobby', 'Spend 1 hour on a hobby', 45, 'hobby', '/icons/hobby.png');

-- Insert achievement definitions
INSERT IGNORE INTO achievement_definitions (name, description, category, criteria_type, criteria_value, points_reward, icon_url) VALUES
('First Steps', 'Complete your first task', 'COMPLETION', 'TASK_COUNT', 1, 50, '/achievements/first.png'),
('Week Warrior', 'Maintain a 7-day streak', 'STREAK', 'STREAK', 7, 100, '/achievements/week.png'),
('Century Club', 'Earn 100 total points', 'POINTS', 'POINTS', 100, 150, '/achievements/century.png'),
('Task Master', 'Complete 25 tasks', 'COMPLETION', 'TASK_COUNT', 25, 200, '/achievements/master.png'),
('BINGO!', 'Complete your first BINGO line', 'BINGO', 'BINGO_LINES', 1, 300, '/achievements/bingo.png'),
('Full House', 'Complete all 25 tasks on a BINGO card', 'BINGO', 'BINGO_LINES', 5, 500, '/achievements/fullhouse.png'),
('Month Master', '30-day streak', 'STREAK', 'STREAK', 30, 1000, '/achievements/month.png'),
('Level 10', 'Reach level 10', 'LEVEL', 'LEVEL', 10, 500, '/achievements/level10.png'),
('Social Butterfly', 'Complete 10 social tasks', 'SOCIAL', 'TASK_COUNT', 10, 300, '/achievements/social.png'),
('Health Nut', 'Complete 15 health tasks', 'HEALTH', 'TASK_COUNT', 15, 350, '/achievements/health.png');

-- Insert admin user (password: admin123 - hashed)
-- Generate BCrypt hash for "admin123" and replace below
INSERT IGNORE INTO users (username, email, password, role) VALUES
('admin', 'admin@bingo.com', '$2a$10$Nk8Zq3B7sV6L9wYhR1dX3uBvC4D5E6F7G8H9I0J1K2L3M4N5O6P7Q8R9S0T1U', 'ADMIN'),
('testuser', 'test@bingo.com', '$2a$10$A1B2C3D4E5F6G7H8I9J0K1L2M3N4O5P6Q7R8S9T0U1V2W3X4Y5Z6a7b8c9d0', 'USER');