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

### `DateEditText` attrs
* `dateFormat`
        * You can choose between two format "ddMMyyyy" or "MMyy" maybe in the future i will add more, you can add and then make pull request. 
        
* `dividerCharacter`
        * You can choose between two divider Character "/" or "-".
        
* `maxDate`
        * set the max date which can user enter it as a string from xml and as a Date object from the code.

* `minDate`
        * set the min date which can user enter it as a string from xml and as a Date object from the code.

* `autoCorrect`
        * if you want to show error alert to user if he enters something invalid set autoCorrect = false, if you want autocorrecting to what the user inputing set it to "true".
        
        
* `helperTextEnabled`
        * show helper text, Used just with TextInputLayout.

* `helperTextHighlightedColor`
        * Highlighted helper text color for what user inputed.


# ScreenShot
![Screenshot_1562874212](https://user-images.githubusercontent.com/17902030/61080508-f147aa00-a42d-11e9-9dcf-bb1586fe8331.png)

# Video

# Developer contact 
   * [Facebook](https://www.facebook.com/profile.php?id=100006656534009)
   * [Twitter](https://twitter.com/salahamassi)
