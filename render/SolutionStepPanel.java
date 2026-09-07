package render;

import solver.SolutionResult;
import solver.SolutionStep;

import javax.swing.*;
import java.awt.*;

public class SolutionStepPanel extends JPanel {

    private static final Color BACKGROUND = new Color(24, 24, 24);
    private static final Color CARD_BG = new Color(32, 32, 34);
    private static final Color CARD_BORDER = new Color(48, 48, 52);
    private static final Color STEP_NUM_COLOR = new Color(90, 180, 255);
    private static final Color DESC_COLOR = new Color(225, 225, 225);
    private static final Color FORMULA_COLOR = new Color(140, 140, 145);
    private static final Color RESULT_COLOR = new Color(120, 255, 150);
    private static final Color ANSWER_BG = new Color(38, 46, 34);
    private static final Color ANSWER_BORDER = new Color(120, 255, 150);
    private static final Color ANSWER_LABEL_COLOR = new Color(160, 160, 160);
    private static final Color ANSWER_VALUE_COLOR = new Color(140, 255, 170);

    private static final Font STEP_NUM_FONT = new Font("Monospaced", Font.BOLD, 13);
    private static final Font DESC_FONT = new Font("Monospaced", Font.PLAIN, 14);
    private static final Font DETAIL_FONT = new Font("Monospaced", Font.PLAIN, 13);
    private static final Font RESULT_FONT = new Font("Monospaced", Font.BOLD, 13);
    private static final Font ANSWER_LABEL_FONT = new Font("Monospaced", Font.PLAIN, 13);
    private static final Font ANSWER_VALUE_FONT = new Font("Monospaced", Font.BOLD, 24);

    public SolutionStepPanel(SolutionResult solution) {
        setBackground(BACKGROUND);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        int stepNumber = 1;
        for (SolutionStep step : solution.steps) {
            JPanel card = buildStepCard(stepNumber, step);
            card.setAlignmentX(Component.LEFT_ALIGNMENT);
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, card.getPreferredSize().height));
            add(card);
            add(Box.createVerticalStrut(10));
            stepNumber++;
        }

        JPanel answerCard = buildAnswerCard(solution);
        answerCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        answerCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, answerCard.getPreferredSize().height));
        add(answerCard);
    }

    private JPanel buildStepCard(int number, SolutionStep step) {
        // A colored left "accent bar" plus padded content, imitating a rounded card
        // look using nested panels -- Swing has no built-in rounded-corner styling
        // without custom painting, so this achieves the visual separation more simply.
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(CARD_BG);
        outer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER, 1),
                BorderFactory.createEmptyBorder(12, 4, 12, 16)
        ));

        JPanel accentBar = new JPanel();
        accentBar.setBackground(STEP_NUM_COLOR);
        accentBar.setPreferredSize(new Dimension(4, 1));
        outer.add(accentBar, BorderLayout.WEST);

        JPanel content = new JPanel();
        content.setBackground(CARD_BG);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 0));

        JLabel numberLabel = new JLabel("STEP " + number);
        numberLabel.setFont(STEP_NUM_FONT);
        numberLabel.setForeground(STEP_NUM_COLOR);
        numberLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel desc = new JLabel(step.description);
        desc.setFont(DESC_FONT);
        desc.setForeground(DESC_COLOR);
        desc.setAlignmentX(Component.LEFT_ALIGNMENT);
        desc.setBorder(BorderFactory.createEmptyBorder(2, 0, 8, 0));

        JLabel formula = new JLabel(step.formula);
        formula.setFont(DETAIL_FONT);
        formula.setForeground(FORMULA_COLOR);
        formula.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel substitution = new JLabel(step.substitution);
        substitution.setFont(DETAIL_FONT);
        substitution.setForeground(FORMULA_COLOR);
        substitution.setAlignmentX(Component.LEFT_ALIGNMENT);
        substitution.setBorder(BorderFactory.createEmptyBorder(2, 0, 6, 0));

        JLabel result = new JLabel(String.format("= %.3f %s", step.result, step.unit));
        result.setFont(RESULT_FONT);
        result.setForeground(RESULT_COLOR);
        result.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(numberLabel);
        content.add(desc);
        content.add(formula);
        content.add(substitution);
        content.add(result);

        outer.add(content, BorderLayout.CENTER);
        return outer;
    }

    private JPanel buildAnswerCard(SolutionResult solution) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(ANSWER_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ANSWER_BORDER, 1),
                BorderFactory.createEmptyBorder(14, 18, 14, 18)
        ));

        JLabel label = new JLabel("FINAL ANSWER");
        label.setFont(ANSWER_LABEL_FONT);
        label.setForeground(ANSWER_LABEL_COLOR);

        JLabel value = new JLabel(String.format("%.3f %s", solution.finalAnswer, solution.finalUnit));
        value.setFont(ANSWER_VALUE_FONT);
        value.setForeground(ANSWER_VALUE_COLOR);
        value.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));

        JPanel textStack = new JPanel();
        textStack.setBackground(ANSWER_BG);
        textStack.setLayout(new BoxLayout(textStack, BoxLayout.Y_AXIS));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        value.setAlignmentX(Component.LEFT_ALIGNMENT);
        textStack.add(label);
        textStack.add(value);

        card.add(textStack, BorderLayout.WEST);
        return card;
    }
}