package seedu.addressbook.parser;

import seedu.addressbook.data.exception.IllegalValueException;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static seedu.addressbook.common.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import java.util.ArrayList;
import java.util.List;

import seedu.addressbook.commands.AddCommand;
import seedu.addressbook.commands.ClearCommand;
import seedu.addressbook.commands.Command;
import seedu.addressbook.commands.DeleteCommand;
import seedu.addressbook.commands.ExitCommand;
import seedu.addressbook.commands.FindCommand;
import seedu.addressbook.commands.HelpCommand;
import seedu.addressbook.commands.IncorrectCommand;
import seedu.addressbook.commands.ListCommand;
import seedu.addressbook.commands.ViewAllCommand;
import seedu.addressbook.commands.ViewCommand;
import seedu.addressbook.commands.UndoCommand;
import seedu.addressbook.commands.RedoCommand;
import seedu.addressbook.state.ApplicationHistory;
import seedu.addressbook.commands.SortableCommand;
import seedu.addressbook.data.tag.Tag;
import seedu.addressbook.data.person.Email;
import seedu.addressbook.data.person.Address;
import seedu.addressbook.data.person.Phone;

/**
 * Parses user input.
 */
public class Parser {

    public static final Pattern PERSON_INDEX_ARGS_FORMAT = Pattern.compile("(?<targetIndex>.+)");
    
    public static final Pattern ARGS_FORMAT =
            Pattern.compile("(?<arguments>\\S+(?:\\s+\\S+)*)"); // one or more arguments separated by whitespace

    public static final Pattern PERSON_DATA_ARGS_FORMAT = // '/' forward slashes are reserved for delimiter prefixes
            Pattern.compile("(?<name>[^/]+)"
                    + " (?<isPhonePrivate>p?)" + Phone.PREFIX + "(?<phone>[^/]+)"
                    + " (?<isEmailPrivate>p?)" + Email.PREFIX + "(?<email>[^/]+)"
                    + " (?<isAddressPrivate>p?)" + Address.PREFIX + "(?<address>[^/]+)"
                    + "(?<tagArguments>(?: "+ Tag.PREFIX + "[^/]+)*)"); // variable number of tags


    /**
     * Signals that the user input could not be parsed.
     */
    public static class ParseException extends Exception {
        ParseException(String message) {
            super(message);
        }
    }

    /**
     * Used for initial separation of command word and args.
     */
    public static final Pattern BASIC_COMMAND_FORMAT = Pattern.compile("(?<commandWord>\\S+)(?<arguments>.*)");

    /**
     * Parses user input into command for execution.
     *
     * @param userInput full user input string
     * @param applicationHistory the application history to be used for history commands.
     * @return the command based on the user input
     */
    public Command parseCommand(String userInput, ApplicationHistory applicationHistory) {
        final Matcher matcher = BASIC_COMMAND_FORMAT.matcher(userInput.trim());
        if (!matcher.matches()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
        }

        final String commandWord = matcher.group("commandWord");
        final String arguments = matcher.group("arguments");
        switch (commandWord) {

            case AddCommand.COMMAND_WORD:
                return prepareAdd(arguments);

            case DeleteCommand.COMMAND_WORD:
                return prepareDelete(arguments);

            case ClearCommand.COMMAND_WORD:
                return new ClearCommand();

            case FindCommand.COMMAND_WORD:
                return prepareFind(arguments);

            case ListCommand.COMMAND_WORD:
                return prepareList(arguments);

            case ViewCommand.COMMAND_WORD:
                return prepareView(arguments);

            case ViewAllCommand.COMMAND_WORD:
                return prepareViewAll(arguments);

            case UndoCommand.COMMAND_WORD:
                return new UndoCommand(applicationHistory);
    
            case RedoCommand.COMMAND_WORD:
                return new RedoCommand(applicationHistory);
                
            case ExitCommand.COMMAND_WORD:
                return new ExitCommand();
    
                case HelpCommand.COMMAND_WORD: // Fallthrough
            default:
                return new HelpCommand();
        }
    }

    /**
     * Parses arguments in the context of the add person command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareAdd(String args){
        final Matcher matcher = PERSON_DATA_ARGS_FORMAT.matcher(args.trim());
        // Validate arg string format
        if (!matcher.matches()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }
        try {
            return new AddCommand(
                    matcher.group("name"),

                    matcher.group("phone"),
                    isPrivatePrefixPresent(matcher.group("isPhonePrivate")),

                    matcher.group("email"),
                    isPrivatePrefixPresent(matcher.group("isEmailPrivate")),

                    matcher.group("address"),
                    isPrivatePrefixPresent(matcher.group("isAddressPrivate")),

                    getTagsFromArgs(matcher.group("tagArguments"))
            );
        } catch (IllegalValueException ive) {
            return new IncorrectCommand(ive.getMessage());
        }
    }

    /**
     * Checks whether the private prefix of a contact detail in the add command's arguments string is present.
     */
    private static boolean isPrivatePrefixPresent(String matchedPrefix) {
        return matchedPrefix.equals("p");
    }

    /**
     * Extracts the new person's tags from the add command's tag arguments string.
     * Merges duplicate tag strings.
     */
    private static Set<String> getTagsFromArgs(String tagArguments) throws IllegalValueException {
        // no tags
        if (tagArguments.isEmpty()) {
            return Collections.emptySet();
        }
        // replace first delimiter prefix, then split
        final Collection<String> tagStrings = Arrays.asList(tagArguments.replaceFirst(" " + Tag.PREFIX, "").split(" t/"));
        return new HashSet<>(tagStrings);
    }


    /**
     * Parses arguments in the context of the delete person command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareDelete(String args) {
        try {
            final int targetIndex = parseArgsAsDisplayedIndex(args);
            return new DeleteCommand(targetIndex);
        } catch (ParseException | NumberFormatException e) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
        }
    }

    /**
     * Parses arguments in the context of the view command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareView(String args) {

        try {
            final int targetIndex = parseArgsAsDisplayedIndex(args);
            return new ViewCommand(targetIndex);
        } catch (ParseException | NumberFormatException e) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    ViewCommand.MESSAGE_USAGE));
        }
    }

    /**
     * Parses arguments in the context of the view all command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareViewAll(String args) {

        try {
            final int targetIndex = parseArgsAsDisplayedIndex(args);
            return new ViewAllCommand(targetIndex);
        } catch (ParseException | NumberFormatException e) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    ViewAllCommand.MESSAGE_USAGE));
        }
    }

    /**
     * Parses the given arguments string as a single index number.
     *
     * @param args arguments string to parse as index number
     * @return the parsed index number
     * @throws ParseException if no region of the args string could be found for the index
     * @throws NumberFormatException the args string region is not a valid number
     */
    private int parseArgsAsDisplayedIndex(String args) throws ParseException, NumberFormatException {
        final Matcher matcher = PERSON_INDEX_ARGS_FORMAT.matcher(args.trim());
        if (!matcher.matches()) {
            throw new ParseException("Could not find index number to parse");
        }
        return Integer.parseInt(matcher.group("targetIndex"));
    }

    /**
     * Parses sort arguments in the context of the list command
     * 
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareList(String args) {
        if (args.trim().matches("")) {
            return new ListCommand(null);
        }
        
        // arguments delimited by whitespace
        final String[] arguments = args.trim().split("\\s+");
        
        // if any argument is not a valid sort argument, return invalid format
        for (String argument : arguments) {
            if (!SortableCommand.isValidSortArgument(argument)) {
                return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        ListCommand.MESSAGE_USAGE)); 
            }
        }

        return new ListCommand(new ArrayList<>(Arrays.asList(arguments)));
    }

    /**
     * Parses arguments in the context of the find person command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareFind(String args) {
        final Matcher matcher = ARGS_FORMAT.matcher(args.trim());
        if (!matcher.matches()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    FindCommand.MESSAGE_USAGE));
        }

        // arguments delimited by whitespace
        final String[] arguments = matcher.group("arguments").split("\\s+");
        final Set<String> keywordSet = new HashSet<>();
        final List<String> sortArgumentList = new ArrayList<>();
        
        // If an argument is a sort argument,
        // stop adding arguments to keywordSet and add remaining to sortArguments.
        // If any of the remaining elements is not a sort argument, return an invalid format.
        for (String argument : arguments) {
            if (!SortableCommand.isValidSortArgument(argument) && sortArgumentList.isEmpty()) {
                keywordSet.add(argument);
            } else if (!SortableCommand.isValidSortArgument(argument) && !sortArgumentList.isEmpty()) {
                return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        FindCommand.MESSAGE_USAGE));
            } else {
                sortArgumentList.add(argument);
            }
        }
        
        return new FindCommand(keywordSet, sortArgumentList);
    }

}