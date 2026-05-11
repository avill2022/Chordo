import os
import sys
from pathlib import Path

def replace_in_kt_files(root_folder, old_sequence, new_sequence, dry_run=False):
    """
    Replace a character sequence in all .KT files within a folder and its subfolders.
    
    Args:
        root_folder: Path to the root folder to scan
        old_sequence: The character sequence to replace
        new_sequence: The new character sequence to insert
        dry_run: If True, only show what would be changed without modifying files
    """
    
    # Convert to Path object for easier handling
    root_path = Path(root_folder)
    
    if not root_path.exists():
        print(f"Error: Folder '{root_folder}' does not exist.")
        return
    
    if not root_path.is_dir():
        print(f"Error: '{root_folder}' is not a directory.")
        return
    
    # Find all .KT files (case-insensitive)
    kt_files = list(root_path.rglob("*.KT")) + list(root_path.rglob("*.kt"))
    # Remove duplicates and keep unique files
    kt_files = list(set(kt_files))
    
    if not kt_files:
        print(f"No .KT or .kt files found in '{root_folder}' or its subfolders.")
        return
    
    print(f"Found {len(kt_files)} KT file(s) to process.")
    print(f"Replacing '{old_sequence}' with '{new_sequence}'")
    print("-" * 60)
    
    modified_count = 0
    total_replacements = 0
    
    for file_path in kt_files:
        try:
            # Read file content
            with open(file_path, 'r', encoding='utf-8') as file:
                content = file.read()
            
            # Count occurrences
            occurrences = content.count(old_sequence)
            
            if occurrences > 0:
                # Perform replacement
                new_content = content.replace(old_sequence, new_sequence)
                
                if dry_run:
                    print(f"[DRY RUN] Would modify: {file_path}")
                    print(f"          {occurrences} occurrence(s) found")
                else:
                    # Write back to file
                    with open(file_path, 'w', encoding='utf-8') as file:
                        file.write(new_content)
                    print(f"[MODIFIED] {file_path}")
                    print(f"           {occurrences} replacement(s) made")
                
                modified_count += 1
                total_replacements += occurrences
            else:
                print(f"[SKIPPED]  {file_path} - No matches found")
                
        except UnicodeDecodeError:
            print(f"[ERROR]    {file_path} - Could not read as UTF-8 text")
        except PermissionError:
            print(f"[ERROR]    {file_path} - Permission denied")
        except Exception as e:
            print(f"[ERROR]    {file_path} - {str(e)}")
    
    print("-" * 60)
    print(f"Summary: {modified_count} file(s) modified, {total_replacements} total replacement(s)")
    
    if dry_run:
        print("\nThis was a DRY RUN. No files were actually modified.")
        print("Remove --dry-run or set dry_run=False to apply changes.")

def main():
    """Main function with command-line interface"""
    
    # Check command line arguments
    if len(sys.argv) < 4:
        print("Usage: python refactor_kt.py <folder_path> <old_sequence> <new_sequence> [--dry-run]")
        print("\nExamples:")
        print("  python refactor_kt.py ./my_project 'OldClass' 'NewClass'")
        print("  python refactor_kt.py ./my_project 'old_function' 'new_function' --dry-run")
        print("\nOptions:")
        print("  --dry-run    Preview changes without modifying files")
        sys.exit(1)
    
    folder_path = sys.argv[1]
    old_sequence = sys.argv[2]
    new_sequence = sys.argv[3]
    
    # Check for dry-run flag
    dry_run = '--dry-run' in sys.argv
    
    # Run the replacement
    replace_in_kt_files(folder_path, old_sequence, new_sequence, dry_run)

if __name__ == "__main__":
    main()