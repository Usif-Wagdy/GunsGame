package gameplay;

import com.sun.opengl.util.Animator;
import com.sun.opengl.util.FPSAnimator;
import java.util.List;
import java.util.ArrayList;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

public class MainMenu extends JFrame {
    GunGLEventListener gunGLEventListener;
    GLCanvas glCanvas;
    private JLabel instructionsImageLabel;  // هنا نعرف المتغير كعضو في الكلاس
    String player1Name;
    String player2Name;
    private static final String IMAGE_PATH = "src\\Assets\\Images\\";
    private ArrayList<String> playerNames = new ArrayList<>();
    private ArrayList<Player> players = new ArrayList<>();
    private ArrayList<Player> highScores = new ArrayList<>();

    private JButton newGameButton;
    private JButton resumeGameButton;
    private JButton highScoresButton;
    private JButton exitButton;
    private JLabel logo;
    private JButton stopSoundButton; // زر لإيقاف الصوت
    private JPanel buttonPanel;
    private JLabel background; // خلفية الصورة
    private Clip clip;
    private boolean isSoundPlaying = false; // لتتبع حالة الصوت

    // زر Single Player و Multiplayer
    private JButton singlePlayerButton;
    private JButton multiplayerButton;

    // زر لعرض خيارات الصعوبة
    private JButton easyButton;
    private JButton mediumButton;
    private JButton hardButton;
    private JButton submitButton1 , submitButton2 , submitButton3;

    public MainMenu() {
        setTitle("TetroGL");
        glCanvas = new GLCanvas();
        setSize(700, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        loadHighScores();

        // إعداد الخلفية
        ImageIcon backgroundIcon = new ImageIcon(IMAGE_PATH + "2203_w023_n001_1968b_p1_1968.jpg");
        Image backgroundImage = backgroundIcon.getImage();
        backgroundImage = backgroundImage.getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH);
        background = new JLabel(new ImageIcon(backgroundImage));
        background.setLayout(new BorderLayout());

        // إعداد الأزرار
        buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 10, 0, 10);
        gbc.gridx = 0;

        // زر New Game
        newGameButton = createImageButton(
                IMAGE_PATH + "Start 1.png",
                IMAGE_PATH + "Start 2.png",
                this::showGameModeOptions);
        newGameButton.addActionListener(e -> {
            showGameModeOptions();
            playClickSound(IMAGE_PATH + "button-click_[cut_3sec].wav");
        });

        gbc.gridy = 0;
        buttonPanel.add(newGameButton, gbc);

        // زر Resume Game
        resumeGameButton = createImageButton(
                IMAGE_PATH + "Resume 1.png",
                IMAGE_PATH + "Resume 2.png",
                () -> System.out.println("Resume Game Pressed"));
        resumeGameButton.addActionListener(e -> {
            playClickSound(IMAGE_PATH + "button-click_[cut_3sec].wav");
        });
        gbc.gridy = 1;
        buttonPanel.add(resumeGameButton, gbc);

        // زر High Scores
        highScoresButton = createImageButton(
                IMAGE_PATH + "High 1.png",
                IMAGE_PATH + "High 2.png",
                () -> showHighScores());
        highScoresButton.addActionListener(e -> {
            playClickSound(IMAGE_PATH + "button-click_[cut_3sec].wav");
        });
        gbc.gridy = 2;
        buttonPanel.add(highScoresButton, gbc);

        // زر Exit
        exitButton = createImageButton(
                IMAGE_PATH  + "Exit 1.png",
                IMAGE_PATH + "exit 2.png",
                () -> System.exit(0));
        exitButton.addActionListener(e -> {
            playClickSound(IMAGE_PATH + "button-click_[cut_3sec].wav");
        });
        gbc.gridy = 3;
        buttonPanel.add(exitButton, gbc);

        // إضافة الأزرار الرئيسية في المركز
        background.add(buttonPanel, BorderLayout.CENTER);

        // إعداد زر إيقاف الصوت في JPanel مع FlowLayout
        JPanel soundButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        soundButtonPanel.setOpaque(false); // حتى لا يؤثر على الخلفية

        stopSoundButton = createSoundButton(
                IMAGE_PATH + "b_Sound2_Inactive.png", // صورة الزر عند التشغيل
                this::toggleSound);

// تغيير حجم زر الصوت ليكون أصغر
        ImageIcon icon = new ImageIcon( IMAGE_PATH + "b_Sound2.png");
        Image image = icon.getImage();
        Image scaledImage = image.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        stopSoundButton.setIcon(new ImageIcon(scaledImage));

// إضافة زر الصوت إلى الـ JPanel
        soundButtonPanel.add(stopSoundButton);


// إضافة الـ JPanel إلى أعلى الخلفية في BorderLayout.NORTH
        background.add(soundButtonPanel, BorderLayout.NORTH);
// إضافة أيقونة جديدة أسفل أو بجانب زر الصوت



        // إنشاء JLabel لعرض صورة التعليمات
        ImageIcon instructionsIcon = new ImageIcon("src\\Assets\\Images\\b_Parameters.png");
        Image iconImage = instructionsIcon.getImage();
        Image scaledIconImage = iconImage.getScaledInstance(50, 50, Image.SCALE_SMOOTH); // تغيير الحجم هنا
        ImageIcon scaledInstructionsIcon = new ImageIcon(scaledIconImage);// استبدل هذا باسم الصورة التي تريد عرضها
        instructionsImageLabel = new JLabel(scaledInstructionsIcon);
// تعيين مستمع للأحداث على الصورة (عند النقر عليها)
        instructionsImageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // عند الضغط على الصورة، إظهار صورة التعليمات
                showInstructions();
            }
        });

// إضافة الصورة إلى الـ panel الموجود في MainMenu
        soundButtonPanel.add(instructionsImageLabel);  // يمكنك إضافة هذه الصورة بجانب زر الصوت

        setContentPane(background);

        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                resizeBackgroundImage();
            }
        });

        setVisible(true);
    }

    public JButton createImageButton(String imagePath, String hoverImagePath, Runnable action) {
        // تحميل الصورة
        ImageIcon icon = new ImageIcon(imagePath);
        Image image = icon.getImage();
        // تعديل الأبعاد هنا (على سبيل المثال 400x150)
        Image scaledImage = image.getScaledInstance(400, 100, Image.SCALE_SMOOTH); // تغيير الأبعاد هنا
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        JButton button = new JButton(scaledIcon);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setMargin(new Insets(0, 0, 0, 0)); // تقليل المساحة حول الزر

        // تغيير الصورة عند مرور الماوس
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // تحميل صورة hover
                ImageIcon hoverIcon = new ImageIcon(hoverImagePath);
                Image hoverImage = hoverIcon.getImage();
                Image scaledHoverImage = hoverImage.getScaledInstance(400, 100, Image.SCALE_SMOOTH); // تغيير الأبعاد هنا
                button.setIcon(new ImageIcon(scaledHoverImage)); // تغيير الصورة
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setIcon(scaledIcon); // إعادة الصورة الأصلية بعد الخروج من الزر
            }
        });

        // عند الضغط على الزر، يتم تنفيذ الإجراء
        button.addActionListener(e -> action.run());
        return button;
    }

    public void showInstructions() {
        // إخفاء الأزرار الحالية (مثل New Game و Resume Game) وصورة التعليمات نفسها
        buttonPanel.setVisible(false);
        instructionsImageLabel.setVisible(false);  // إخفاء الأيقونة أيضًا

        // تحميل صورة التعليمات (التي ستصبح الخلفية)
        ImageIcon instructionsImage = new ImageIcon(IMAGE_PATH + "HOW TO PLAY.png");  // استبدل بـ اسم الصورة
        Image instructionsImg = instructionsImage.getImage();
        Image scaledImg = instructionsImg.getScaledInstance((getWidth() / 2) + 50 , (getHeight() / 2) + 50  , Image.SCALE_SMOOTH);  // جعل الصورة تملأ الشاشة

        // إضافة الصورة إلى الخلفية
        JLabel instructionsLabel = new JLabel(new ImageIcon(scaledImg));
        instructionsLabel.setLayout(new BorderLayout());  // التأكد من استخدام BorderLayout لتحديد مكان الزر

        // زر العودة (الذي يكون صورة)
        JButton returnButton = createImageButton(
                IMAGE_PATH + "Return 1.png",  // استبدل بـ صورة زر العودة
                IMAGE_PATH + "Return 2.png",  // استبدل بـ نفس الصورة
                () -> {
                    // إخفاء تعليمات والعودة للواجهة الرئيسية
                    instructionsLabel.setVisible(false); // إخفاء صورة التعليمات
                    buttonPanel.setVisible(true);  // إظهار الأزرار الأصلية مرة أخرى
                    instructionsImageLabel.setVisible(true);  // إعادة الأيقونة لتكون مرئية
                }
        );

        returnButton.setPreferredSize(new Dimension(100, 100));  // تحديد حجم زر العودة
        returnButton.setBorderPainted(false);
        returnButton.setContentAreaFilled(false);

        // إضافة زر العودة إلى الـ instructionsLabel بدلاً من background
        instructionsLabel.add(returnButton, BorderLayout.SOUTH); // إضافة الزر داخل التعليمات

        // إضافة الصورة إلى الـ background
        background.add(instructionsLabel, BorderLayout.CENTER);

        // إعادة رسم الواجهة لتحديث التغييرات
        revalidate();
        repaint();
    }

    private JButton createSoundButton(String imagePath, Runnable action) {
        // تحميل الصورة
        ImageIcon icon = new ImageIcon(imagePath);
        Image image = icon.getImage();
        Image scaledImage = image.getScaledInstance(50, 50, Image.SCALE_SMOOTH); // تغيير الأبعاد هنا
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        JButton button = new JButton(scaledIcon);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);

        // إضافة action لإيقاف الصوت عند الضغط على الزر
        button.addActionListener(e -> action.run());

        return button;
    }
    private void resizeBackgroundImage() {
        ImageIcon backgroundIcon = new ImageIcon(IMAGE_PATH + "2203_w023_n001_1968b_p1_1968.jpg");
        Image backgroundImage = backgroundIcon.getImage();
        backgroundImage = backgroundImage.getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH);
        background.setIcon(new ImageIcon(backgroundImage));
    }
    public void playSound(String soundFileName) {
        try {
            if (clip == null || !clip.isOpen()) {
                File soundFile = new File(soundFileName);
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
                clip = AudioSystem.getClip();
                clip.open(audioStream);
            }
            clip.setFramePosition(0); // إعادة الموضع إلى بداية الملف
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
    public void playClickSound(String soundFileName) {
        try {
            File soundFile = new File(soundFileName);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            Clip clickClip = AudioSystem.getClip();
            clickClip.open(audioStream);
            clickClip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
    // دالة لتبديل الصوت والصورة
    public void toggleSound() {
        if (isSoundPlaying) {
            stopSound();
            isSoundPlaying = false;
            // تغيير صورة الزر عند التوقف (الموسيقى متوقفة)
            ImageIcon icon = new ImageIcon(IMAGE_PATH + "b_Sound2_Inactive.png");
            Image image = icon.getImage();
            Image scaledImage = image.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            stopSoundButton.setIcon(new ImageIcon(scaledImage));
        } else {
            playSound("src\\Assets\\sounds\\Counter Strike Theme Song (1.6 Main Menu) by Counter- Strike-Valve Games_[cut_109sec].wav");
            isSoundPlaying = true;
            // تغيير صورة الزر عند التشغيل (الموسيقى شغالة)
            ImageIcon icon = new ImageIcon(IMAGE_PATH + "b_Sound2.png");
            Image image = icon.getImage();
            Image scaledImage = image.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            stopSoundButton.setIcon(new ImageIcon(scaledImage));
        }
    }

    // دالة لإيقاف الصوت
    public void stopSound() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            clip.setFramePosition(0); // إعادة الموضع إلى بداية الملف
        }
    }
    private void showNameInputPage(boolean isMultiPlayer) {
        buttonPanel.removeAll(); // إزالة كل الأزرار السابقة

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 10, 10, 10);

        // تحديد حجم الـ JTextField
        JTextField player1NameField = new JTextField(20);
        JTextField player2NameField = new JTextField(20);

        // تحديد أبعاد الـ JTextField
        player1NameField.setPreferredSize(new Dimension(200, 30));  // تحديد الطول والعرض للـ JTextField الأول
        player2NameField.setPreferredSize(new Dimension(200, 30));  // تحديد الطول والعرض للـ JTextField الثاني

        // اسم اللاعب الأول
        JLabel player1Label = new JLabel("Enter Player 1 Name:");
        player1Label.setFont(new Font("Georgia", Font.BOLD, 18));
        player1Label.setForeground(Color.black);
        gbc.gridy = 0;
        buttonPanel.add(player1Label, gbc);

        gbc.gridy = 1;
        buttonPanel.add(player1NameField, gbc);

        if (isMultiPlayer) {
            // اسم اللاعب الثاني إذا كان Multiplayer
            JLabel player2Label = new JLabel("Enter Player 2 Name:");
            player2Label.setFont(new Font("Georgia", Font.BOLD, 18));
            player2Label.setForeground(Color.black);
            gbc.gridy = 2;
            buttonPanel.add(player2Label, gbc);

            gbc.gridy = 3;
            buttonPanel.add(player2NameField, gbc);
        }

        // زر Submit كصورة
        JButton submitButton = createImageButton(
                "src\\Assets\\Images\\SUBMIT2.png",  // الصورة عند الحالة العادية
                "src\\Assets\\Images\\SUBMIT.png",   // الصورة عند تمرير الماوس
                () -> {
                    playClickSound(IMAGE_PATH + "button-click_[cut_3sec].wav");

                    player1Name = player1NameField.getText().trim();
                    player2Name = isMultiPlayer ? player2NameField.getText().trim() : null;

                    // التأكد من أنه تم إدخال جميع الأسماء
                    if (player1Name.isEmpty() || (isMultiPlayer && player2Name.isEmpty())) {
                        JOptionPane.showMessageDialog(this, "Please enter all names!", "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        // إضافة الأسماء إلى playerNames
                        System.out.println("Player 1: " + player1Name);
                        if (isMultiPlayer) {
                            System.out.println("Player 2: " + player2Name);
                        }

                        playerNames.add(player1Name);
                        if (isMultiPlayer) playerNames.add(player2Name);

                        // حفظ الأسماء بعد التعديل
                        saveHighScores();

                        // الانتقال لصفحة اختيار الصعوبة
                        showDifficultyOptions(isMultiPlayer);
                    }

                });
        // إضافة الزر إلى الـ panel الموجود في MainMenu
        gbc.gridy = isMultiPlayer ? 4 : 2; // تحديد الموقع بناءً على الـ isMultiPlayer
        buttonPanel.add(submitButton, gbc);

        // زر Back
        JButton backButton = createImageButton(
                IMAGE_PATH + "Return 1.png",
                IMAGE_PATH + "Return 2.png",
                this::showGameModeOptions);
        backButton.addActionListener(e -> playClickSound(IMAGE_PATH + "button-click_[cut_3sec].wav"));
        gbc.gridy++;
        buttonPanel.add(backButton, gbc);

        // زر Clear
        JButton clearButton = createImageButton(
                IMAGE_PATH + "Clear 1.png",  // الصورة عند الحالة العادية
                IMAGE_PATH + "Clear 2.png",  // الصورة عند تمرير الماوس
                () -> {
                    clearHighScores();
                    showHighScores();  // تحديث العرض بعد الحذف
                });

        // تعيين الحجم المفضل للزر
        clearButton.setPreferredSize(new Dimension(200, 50));

        // إضافة زر Clear إلى الـ panel
        gbc.gridy++;
        buttonPanel.add(clearButton, gbc);

        // إعادة الرسم
        buttonPanel.revalidate();
        buttonPanel.repaint();
    }


    private void showHighScores() {
        buttonPanel.removeAll();  // إزالة كل الأزرار السابقة

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 10, 10, 10);

        // إنشاء JPanel لعرض الـ High Scores
        JPanel highScoresPanel = new JPanel();
        highScoresPanel.setLayout(new BoxLayout(highScoresPanel, BoxLayout.Y_AXIS));

        // التحقق إذا كانت قائمة الـ highScores فارغة
        if (highScores.isEmpty()) {
            JLabel noScoresLabel = new JLabel("No high scores yet.");
            noScoresLabel.setFont(new Font("Georgia", Font.PLAIN, 18));  // تغيير نوع الخط
            noScoresLabel.setForeground(Color.WHITE);  // تعيين اللون الأبيض
            highScoresPanel.add(noScoresLabel);
        } else {
            // ترتيب اللاعبين بناءً على النتيجة من الأعلى إلى الأقل
            highScores.sort((p1, p2) -> Integer.compare(p2.getScore(), p1.getScore()));

            // إضافة كل لاعب في القائمة إلى الـ JPanel
            for (Player player : highScores) {
                JLabel nameLabel = new JLabel(player.getName() + ": " + player.getScore());
                nameLabel.setFont(new Font("Arial", Font.BOLD, 18));  // تغيير نوع الخط إلى Arial بالخط العريض
                nameLabel.setForeground(Color.GREEN);  // تعيين اللون الأصفر
                highScoresPanel.add(nameLabel);
            }
        }

        // تعيين الصورة كخلفية للمحتوى
        JScrollPane scrollPane = new JScrollPane(highScoresPanel);
        scrollPane.setPreferredSize(new Dimension(400, 300));  // تحديد حجم التمرير

        // إضافة الـ JLabel كـ Viewport داخل الـ JScrollPane
        scrollPane.setViewportView(highScoresPanel);  // تأكد من أن الـ highScoresPanel هو الـ viewport وليس الـ JLabel
        highScoresPanel.setOpaque(false);  // لجعل الـ JPanel شفافاً لكي تظهر الصورة خلفه

        // إضافة الـ JScrollPane إلى الـ buttonPanel
        gbc.gridy = 0;
        buttonPanel.add(scrollPane, gbc);

        // زر الرجوع إلى القائمة الرئيسية
        JButton backButton = createImageButton(
                IMAGE_PATH + "Return 1.png",
                IMAGE_PATH + "Return 2.png",
                this::showMainMenu);
        backButton.addActionListener(e -> {
            playClickSound(IMAGE_PATH + "button-click_[cut_3sec].wav");
        });
        gbc.gridy = 2;  // وضع الزر في أسفل الصفحة
        buttonPanel.add(backButton, gbc);

        // إعادة الرسم بعد التغيير
        buttonPanel.revalidate();
        buttonPanel.repaint();

        JButton clearButton = createImageButton(
                IMAGE_PATH + "CLEAR.png",
                "src\\Assets\\Images\\CLEAR1.png",
                () -> {
                    clearHighScores();
                    showHighScores();  // تحديث العرض بعد الحذف
                });
        backButton.addActionListener(e -> {
            playClickSound(IMAGE_PATH + "button-click_[cut_3sec].wav");
        });
        gbc.gridy = 1;  // وضع الزر أسفل الزر "Back"
        buttonPanel.add(clearButton, gbc);

    }


    private void clearHighScores() {
        highScores.clear();  // مسح الأسماء المخزنة في highScores
        saveHighScores();    // حفظ الملف بعد مسح الأسماء
    }


    // دالة لإضافة نتيجة لاعب
    public void addPlayerScore(String playerName, int score) {
        Player player = new Player(playerName, score);
        highScores.add(player);  // إضافة اللاعب إلى قائمة highScores
        saveHighScores();  // حفظ البيانات في الملف بعد إضافة النتيجة
    }

    public void saveHighScores() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("highscores.txt"))) {
            for (Player player : highScores) {
                writer.write(player.getName() + "," + player.getScore());
                writer.newLine();
            }
            System.out.println("High scores saved successfully!");  // رسالة تأكيد الكتابة
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadHighScores() {
        highScores.clear();  // مسح القائمة الحالية من الـ highScores
        try (BufferedReader reader = new BufferedReader(new FileReader("highscores.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String name = parts[0];
                    int score = Integer.parseInt(parts[1]);
                    highScores.add(new Player(name, score));  // إضافة اللاعب إلى القائمة
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void showGameModeOptions() {
        buttonPanel.removeAll(); // إزالة الأزرار السابقة

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;

        // زر Single Player
        singlePlayerButton = createImageButton(
                IMAGE_PATH + "Single 1.png",
                IMAGE_PATH + "Sinlge 2.png",
                () -> showNameInputPage(false)); // تمرير false لأن الوضع Single Player
        singlePlayerButton.addActionListener(e -> {
            playClickSound(IMAGE_PATH + "button-click_[cut_3sec].wav");
        });
        gbc.gridy = 0;
        buttonPanel.add(singlePlayerButton, gbc);

        // زر Multiplayer
        multiplayerButton = createImageButton(
                IMAGE_PATH + "Multi 1.png",
                IMAGE_PATH + "Multi 2.png",
                () -> showNameInputPage(true)); // تمرير true لأن الوضع Multiplayer
        multiplayerButton.addActionListener(e -> {
            playClickSound(IMAGE_PATH + "button-click_[cut_3sec].wav");
        });
        gbc.gridy = 1;  // الصف الثاني
        buttonPanel.add(multiplayerButton, gbc);

        // زر Back للعودة إلى القائمة الرئيسية
        JButton backButton = createImageButton(
                IMAGE_PATH + "Return 1.png",
                IMAGE_PATH + "Return 2.png",
                this::showMainMenu);
        backButton.addActionListener(e -> {
            playClickSound(IMAGE_PATH + "button-click_[cut_3sec].wav");
        });
        gbc.gridy = 2; // وضع الزر في الصف الثالث
        buttonPanel.add(backButton, gbc);

        // إعادة الرسم بعد التغيير
        buttonPanel.revalidate();
        buttonPanel.repaint();
    }

    public void showMainMenu() {
        buttonPanel.removeAll(); // إزالة جميع الأزرار

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 10, 0, 10);
        gbc.gridx = 0;

        // زر New Game
        newGameButton = createImageButton(
                IMAGE_PATH + "Start 1.png",
                IMAGE_PATH + "Start 2.png",
                this::showGameModeOptions);
        newGameButton.addActionListener(e -> {
            showGameModeOptions();
            playClickSound(IMAGE_PATH + "button-click_[cut_3sec].wav");
        });
        gbc.gridy = 0;
        buttonPanel.add(newGameButton, gbc);

        // زر Resume Game
        resumeGameButton = createImageButton(
                IMAGE_PATH + "Resume 1.png",
                IMAGE_PATH + "Resume 2.png",
                () -> System.out.println("Resume Game Pressed"));
        resumeGameButton.addActionListener(e -> {
            playClickSound(IMAGE_PATH + "button-click_[cut_3sec].wav");
        });
        gbc.gridy = 1;
        buttonPanel.add(resumeGameButton, gbc);

        // زر High Scores
        highScoresButton = createImageButton(
                IMAGE_PATH + "High 1.png",
                IMAGE_PATH + "High 2.png",
                () -> showHighScores());
        highScoresButton.addActionListener(e -> {
            playClickSound(IMAGE_PATH + "button-click_[cut_3sec].wav");
        });
        gbc.gridy = 2;
        buttonPanel.add(highScoresButton, gbc);
        // زر Exit
        exitButton = createImageButton(
                IMAGE_PATH + "Exit 1.png",
                IMAGE_PATH + "exit 2.png",
                () -> System.exit(0));
        exitButton.addActionListener(e -> {
            playClickSound(IMAGE_PATH + "button-click_[cut_3sec].wav");
        });
        gbc.gridy = 3;
        buttonPanel.add(exitButton, gbc);

        // إعادة الرسم بعد التغيير
        buttonPanel.revalidate();
        buttonPanel.repaint();
    }
    private void showDifficultyOptions(boolean isMultiPlayer) {
        buttonPanel.removeAll(); // إزالة الأزرار السابقة

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;

        // إضافة الأزرار فقط بدون تفاصيل حول الأسماء
        // زر Easy
        easyButton = createImageButton(
                IMAGE_PATH + "EASY 1 (1).png",
                IMAGE_PATH + "EASY (1).png",
                () -> startGame(player1Name , "Easy"));
        easyButton.addActionListener(e -> {
            playClickSound(IMAGE_PATH + "button-click_[cut_3sec].wav");
            startGame(player1Name, "Easy");
        });
        gbc.gridy = 0;
        buttonPanel.add(easyButton, gbc);

        // زر Medium
        gbc.gridy++;
        mediumButton = createImageButton(
                IMAGE_PATH + "Medium 2  .png",
                IMAGE_PATH + "Medium .png",
                () -> startGame(player1Name, "Medium"));
        mediumButton.addActionListener(e -> {
            playClickSound(IMAGE_PATH + "button-click_[cut_3sec].wav");
        });
        buttonPanel.add(mediumButton, gbc);

        // زر Hard
        gbc.gridy++;
        hardButton = createImageButton(
                IMAGE_PATH + "HARD 2 .png",
                IMAGE_PATH + "HARD.png",
                () -> startGame(player1Name, "Hard"));
        hardButton.addActionListener(e -> {
            playClickSound(IMAGE_PATH + "button-click_[cut_3sec].wav");
        });
        buttonPanel.add(hardButton, gbc);

        // زر Back
        gbc.gridy++;
        JButton backButton = createImageButton(
                IMAGE_PATH + "Return 1.png",
                IMAGE_PATH + "Return 2.png",
                this::showGameModeOptions);
        backButton.addActionListener(e -> {
            playClickSound(IMAGE_PATH + "button-click_[cut_3sec].wav");
        });
        buttonPanel.add(backButton, gbc);

        // إعادة الرسم بعد التغيير
        buttonPanel.revalidate();
        buttonPanel.repaint();
    }

    private void startGame(String playerName, String difficulty) {
        getContentPane().removeAll();  // إزالة كل العناصر الحالية
        getContentPane().setLayout(new BorderLayout());  // ضبط التخطيط

        // التأكد من تهيئة gunGLEventListener قبل استخدامه
        if (gunGLEventListener == null) {
            gunGLEventListener = new GunGLEventListener(this, playerName, difficulty);  // تمرير المستوى واسم اللاعب
        }

        GLCanvas glcanvas = new GLCanvas();
        glcanvas.addGLEventListener(gunGLEventListener);
        glcanvas.addKeyListener(gunGLEventListener);  // الاستماع إلى ضغطة المفاتيح
        getContentPane().add(glcanvas, BorderLayout.CENTER);  // إضافة الـ GLCanvas إلى النافذة

        FPSAnimator animator = new FPSAnimator(glcanvas, 15);  // إعداد معدل الإطارات
        animator.start();  // بدء الأنيماتور

        setTitle("Game Started");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 700);
        setLocationRelativeTo(null);
        setVisible(true);  // إظهار النافذة
        setFocusable(true);
        glcanvas.requestFocus();  // طلب التركيز على الـ Canvas

        if (clip != null) clip.stop();  // إيقاف الصوت إذا كان قيد التشغيل

        revalidate();  // إعادة رسم واجهة المستخدم
        repaint();
    }



    public static void main(String[] args) {
        MainMenu mainMenu = new MainMenu();
        mainMenu.playSound("src\\Assets\\sounds\\Counter Strike Theme Song (1.6 Main Menu) by Counter- Strike-Valve Games_[cut_109sec].wav");
        mainMenu.isSoundPlaying = true; // تحديد أن الصوت بدأ بالفعل

    }
}
