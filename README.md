# Android Mask Date EditText

# What is it? 
This library simplified the way of get date from user input, Allows the user to enter a date as a text and then verify it based on date format, divider character and min or max date with other options.

------ 

# How to use it? 

## First Install

```gradle

implementation 'com.github.salahamassi:android-mask-date-editText:v1.01'

```
------ 

## Second Usage
1- Define new DateEdit text inside you layout xml
```xml
  <com.msa.dateedittext.DateEditText
                android:id="@+id/dateEditText"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"/>
```
2- start listening to user input from your acticity, fragment or any android UI components
```kotlin
   dateEditText.listen()
```
and just that's it!

------ 

## More options
please download the sample and watch the video to dive deep more.

### `dividerCharacter`
xml attribute or you can set it programmatically you can choise from tow options

#### `slash`
"/"

#### `minus`
"-"

### `dateFormat`
xml attribute or you can set it programmatically you can choise from tow options

#### `ddMMyyyy`
"01-01-2000"

#### `MMyy`
"01-01"


# Developer contact 
   * [Facebook](https://www.facebook.com/profile.php?id=100006656534009)
   * [Twitter](https://twitter.com/salahamassi)
