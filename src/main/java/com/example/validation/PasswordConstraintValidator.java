package com.example.validation;

import org.passay.*;
import org.springframework.context.annotation.PropertySource;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

@PropertySource("classpath:/props/passayMessages.properties")
public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public void initialize(ValidPassword arg0) {
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        List<Rule> rules = new ArrayList<>();
        rules.add(new LengthRule(8, 30));
        rules.add(new UppercaseCharacterRule(1));
        rules.add(new DigitCharacterRule(1));
        rules.add(new SpecialCharacterRule(1));
        rules.add(new NumericalSequenceRule(3,false));
        rules.add(new AlphabeticalSequenceRule(3,false));
        rules.add(new WhitespaceRule());

        Properties props = new Properties();
        try {
            props.load(new FileInputStream("C:\\Users\\Матвей Батищев\\Desktop\\Моя папка\\Java\\SpringApplication\\src\\main\\resources\\props\\passayMessages.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        MessageResolver resolver = new PropertiesMessageResolver(props);
        PasswordValidator validator = new PasswordValidator(resolver, rules);

        RuleResult result = validator.validate(new PasswordData(password));
        if (result.isValid()) {
            return true;
        }
        List<String> messages = validator.getMessages(result);

        String messageTemplate = messages.stream()
                .collect(Collectors.joining(","));
        context.buildConstraintViolationWithTemplate(messageTemplate)
                .addConstraintViolation()
                .disableDefaultConstraintViolation();
        return false;
    }
}
