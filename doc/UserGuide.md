# User Guide

This product is not meant for end-users and therefore there is no user-friendly installer. 
Please refer to the [Setting up](DeveloperGuide.md#setting-up) section to learn how to set up the project.

## Starting the program

1. Find the project pane (usually located at the left side)
2. Open up `src/seedu.addressbook` folder
3. Right click on `Main`
4. Click `Run Main.main()`
5. The GUI should appear in a few seconds

![](images/Ui.png)

## Viewing help : `help`
Format: `help`

> Help is also shown if you enter an incorrect command e.g. `abcd`
 
## Adding a person: `add`
Adds a person to the address book  
Format: `add NAME [p]p/PHONE_NUMBER [p]e/EMAIL [p]a/ADDRESS [t/TAG]...` 
 
> Words in `UPPER_CASE` are the parameters, items in `SQUARE_BRACKETS` are optional, 
> items with `...` after them can have multiple instances. Order of parameters are fixed. 
> 
> Put a `p` before the phone / email / address prefixes to mark it as `private`. `private` details can only
> be seen using the `viewall` command.
> 
> Persons can have any number of tags (including 0)

Examples: 
* `add John Doe p/98765432 e/johnd@gmail.com a/John street, block 123, #01-01`
* `add Betsy Crowe pp/1234567 e/betsycrowe@gmail.com pa/Newgate Prison t/criminal t/friend`

### Listing all persons : `list`
Shows a list of all persons in the address book, sorted by addition order or the optional specified order.<br>
Format: `list [SORT_ARGUMENTS]`

>Items in square brackets are the optional sort parameters.<br/>
>`SORT_ARGUMENTS` can be any, some or all of
>`[n/]`, `[p/]`, `[e/]`, `[a/]`, `[n/desc]`, `[p/desc]`, `[e/desc]`, `[a/desc]`, `[n/asc]`, `[p/asc]`, `[e/asc]`, `[a/asc]`
>, separated by whitespaces in any order, where `asc` is the optional postfix specifier for an ascending order and 
>`desc` is the optional postfix specifier for a descending order. The sort order prefix will correspond to the following fields:<br/>
>`n/` - the person's name<br/>
>`p/` - the person's phone number<br/>
>`e/` - the person's email<br/>
>`a/` - the person's address
>
>If the postfix is left blank, the sort order of the preceding sort parameter will be ascending by default.
>
>`SORT_ARGUMENTS` should be specified in order of priority, where the leftmost sort parameter has the highest priority.

Examples: 
* `list` <br>
  Returns all persons, sorted by the addition order
* `list n/desc p/` <br>
  Returns all persons, sorted by descending name, then by phone number
* `list p/ n/desc` <br>
  Returns all persons, sorted by phone number, then by descending name
* `list a/desc` <br>
  Returns all persons, sorted by descending address

### Finding all persons containing any keyword in their name: `find`
Finds persons whose names contain any of the given keywords, sorted by addition order or the optional specified order.<br>
Format: `find KEYWORD [MORE_KEYWORDS] [SORT_ARGUMENTS]`

> The search is case sensitive, the order of the keywords does not matter, only the name is searched, 
and persons matching at least one keyword will be returned (i.e. `OR` search).

Examples: 
* `find John`  
  Returns `John Doe` but not `john`
* `find Betsy Tim John`<br>
  Returns any person having names `Betsy`, `Tim`, or `John`
* `find Betsy n/`<br>
  Returns any persons having names `Betsy`, sorted by name
* `find Tim p/desc a/`<br>
  Returns any person having names `Tim`, sorted by descending phone number, then by address

## Deleting a person : `delete`
Deletes the specified person from the address book. Irreversible.  
Format: `delete INDEX`

> Deletes the person at the specified `INDEX`. 
  The index refers to the index number shown in the most recent listing.

Examples: 
* `list`  
  `delete 2`  
  Deletes the 2nd person in the address book.
* `find Betsy`   
  `delete 1`  
  Deletes the 1st person in the results of the `find` command.

## View non-private details of a person : `view`
Displays the non-private details of the specified person.  
Format: `view INDEX`

> Views the person at the specified `INDEX`. 
  The index refers to the index number shown in the most recent listing.

Examples: 
* `list`  
  `view 2`  
  Views the 2nd person in the address book.
* `find Betsy`    
  `view 1`  
  Views the 1st person in the results of the `find` command.

## View all details of a person : `viewall`
Displays all details (including private details) of the specified person.  
Format: `viewall INDEX`

> Views all details of the person at the specified `INDEX`. 
  The index refers to the index number shown in the most recent listing.

Examples: 
* `list`  
  `viewall 2`  
  Views all details of the 2nd person in the address book.
* `find Betsy`   
  `viewall 1`  
  Views all details of the 1st person in the results of the `find` command.

## Clearing all entries : `clear`
Clears all entries from the address book.  
Format: `clear`  

### Undoing a previous command : `undo`
Undoes the previous operation. Will display the new listing and other changes. Undo history will be cleared on application exit.<br>
Format: `undo`  

### Redoing a previous undo command : `redo`
Redoes the previous undo operation. Will display the new listing and other changes. Redo history will be cleared on a new normal command and application exit.<br>
Format: `redo`  

#### Exiting the program : `exit`
Exits the program.<br>
Format: `exit`  

## Saving the data 
Address book data are saved in the hard disk automatically after any command that changes the data.  
There is no need to save manually. Address book data are saved in a file called `addressbook.txt` in the project root folder.
