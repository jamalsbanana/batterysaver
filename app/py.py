#!/usr/bin/env python3

import os
import pyperclip

def find_files(directory, files_to_find):
    found_files = []
    for dirpath, dirnames, filenames in os.walk(directory):
        for filename in filenames:
            if filename in files_to_find:
                found_files.append(os.path.join(dirpath, filename))
    return found_files

def display_text_files(found_files):
    """
    Display the contents of text files.
    """
    text_files_output = "\n"
    for file_path in found_files:
        text_files_output += f"File: {file_path}\n"
        text_files_output += "=" * 30 + "\n"
        with open(file_path, "r") as text_file:
            text_files_output += text_file.read() + "\n\n"
    return text_files_output

def main():
    current_directory = os.getcwd()
    files_to_find = [
        'AndroidManifest.xml',
        'RecordService.java',
        'MainActivity.java',
        'build.gradle',
        'settings.gradle'
    ]

    found_files = find_files(current_directory, files_to_find)

    if found_files:
        text_files_content = display_text_files(found_files)

        # Combine the text files content
        output_content = text_files_content

        # Copy the output to the clipboard
        pyperclip.copy(output_content)

        print("Files found and content copied to the clipboard.")
    else:
        print("No files found.")

if __name__ == "__main__":
    main()
