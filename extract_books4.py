import sys
import os
import requests
import re
import urllib.request
import time

sys.path.insert(0, r'C:\Python314\Lib\site-packages')
import fitz

BOOKS_DIR = r'C:\Users\heiselaizer5\Desktop\LibrarySystemWeb\books'

BOOKS = {
    'T011': {  # Advanced Chemistry
        'label': 'Advanced Chemistry',
        'files': [
            ('General & Inorganic Chemistry (new)', '1epCfCWnHqCN94M2u9PWMf1DXu1K-8-WK'),
            ('Physical Chemistry (new)', '1CMilXSwDvw9S-yzIDL54lNEBCL5dPFuh'),
            ('General Chemistry (old)', '1_IRba0gHF6awve0aTGp_H2QKXoxX_wrA'),
            ('Inorganic Chemistry (old)', '1WdBntuc5UFhDSfjAL4ht0mcqSqqxJ-oP'),
            ('Organic Chemistry (old)', '15vv6demd-LdvYPVP9WF-R2c1joeq5mnF'),
        ]
    },
    'T017': {  # Economics
        'label': 'Economics',
        'files': [
            ('Microeconomics (new)', '1Aq7awfn3oUXFwbyLhC5KH_XUPincjaoj'),
            ('Macro Economics (old)', '1M-wpPUfRq7qY0dpOWbre1cntN2ZINo9t'),
            ('Micro Economics (old)', '1oJ6qj2fDEugGrjQu_NHcXWJdxMVd-vuz'),
        ]
    },
    'T018': {  # Commerce -> Business Studies
        'label': 'Commerce / Business Studies',
        'files': [
            ('Business Studies (new)', '1okZEdIn3qR4pdpinI7GVRjlMXQyu7a-j'),
        ]
    },
    'T010': {  # Physics - try different links
        'label': 'Advanced Physics',
        'files': [
            ('Physics Teachers Guide (new)', '11KE03YGcNg6rmO8XGw0bNFjDt9NvRDHm'),
            ('Physics (old)', '1ygS4TIcQhL7LjnlADffugjZw7KyADtJr'),
        ]
    },
    'T015': {  # English - try old version
        'label': 'English Language',
        'files': [
            ('English (old)', '1mC3QaVd9ylxoj-P2OvbTM94NXE73uSYs'),
        ]
    },
    'T022': {  # Computer Science - old syllabus only
        'label': 'Computer Science',
        'files': [
            ('Computer Science (old)', '1tnxDa9OyARC7Cgm3z_bfDx9eBOFe0xiI'),
        ]
    },
    'T020': {  # French
        'label': 'French',
        'files': [
            ('French (unknown ID)', '14CV4QHteLu3gKNGRtW4Y2maa27UIOrzD'),
        ]
    },
}

def download_google_drive(file_id, dest_path):
    url = f"https://drive.google.com/uc?export=download&id={file_id}"
    session = requests.Session()
    
    print(f"  Downloading file_id={file_id}...")
    response = session.get(url, stream=True, allow_redirects=True)
    
    if 'text/html' in response.headers.get('Content-Type', ''):
        # Check for confirmation dialog
        match = re.search(r'confirm=([0-9A-Za-z_\-]+)', response.text)
        if match:
            confirm_token = match.group(1)
            url = f"https://drive.google.com/uc?export=download&confirm={confirm_token}&id={file_id}"
            response = session.get(url, stream=True, allow_redirects=True)
    
    # Determine file size
    content_length = response.headers.get('Content-Length')
    file_size = int(content_length) if content_length else -1
    print(f"  File size: {file_size} bytes ({file_size/1024/1024:.1f} MB)" if file_size > 0 else f"  File size: unknown")
    
    with open(dest_path, 'wb') as f:
        for chunk in response.iter_content(chunk_size=8192):
            if chunk:
                f.write(chunk)
    
    actual_size = os.path.getsize(dest_path)
    print(f"  Downloaded {actual_size} bytes to {dest_path}")
    return actual_size

def extract_text_from_pdf(pdf_path):
    try:
        doc = fitz.open(pdf_path)
        total_chars = 0
        text_pages = 0
        total_pages = len(doc)
        
        for i, page in enumerate(doc):
            text = page.get_text()
            if text and len(text.strip()) > 50:
                text_pages += 1
            total_chars += len(text)
        
        doc.close()
        return total_chars, text_pages, total_pages
    except Exception as e:
        print(f"  Error opening PDF: {e}")
        return 0, 0, 0

def main():
    os.makedirs(BOOKS_DIR, exist_ok=True)
    
    for isbn, info in BOOKS.items():
        print(f"\n{'='*60}")
        print(f"TARGET: {isbn} - {info['label']}")
        print(f"{'='*60}")
        
        # Check if TXT already exists
        txt_path = os.path.join(BOOKS_DIR, f'{isbn}.txt')
        if os.path.exists(txt_path) and os.path.getsize(txt_path) > 10000:
            print(f"  Already has content ({os.path.getsize(txt_path)} bytes), skipping")
            continue
        
        best_chars = 0
        best_text = ""
        best_source = ""
        
        for file_label, file_id in info['files']:
            print(f"\n  Trying: {file_label}")
            pdf_path = os.path.join(BOOKS_DIR, f'_temp_{file_id}.pdf')
            
            try:
                dl_size = download_google_drive(file_id, pdf_path)
                
                chars, text_pages, total_pages = extract_text_from_pdf(pdf_path)
                print(f"  Extracted: {chars} chars from {text_pages}/{total_pages} pages with text")
                
                if chars > best_chars:
                    best_chars = chars
                    best_source = file_label
                    try:
                        doc = fitz.open(pdf_path)
                        full_text = ""
                        for page in doc:
                            full_text += page.get_text()
                        doc.close()
                        best_text = full_text
                    except:
                        pass
                
                # Clean up PDF
                os.remove(pdf_path)
                print(f"  Deleted temp PDF")
                
            except Exception as e:
                print(f"  Failed: {e}")
                if os.path.exists(pdf_path):
                    os.remove(pdf_path)
                continue
        
        print(f"\n  BEST RESULT: {best_source} with {best_chars} chars")
        
        if best_chars > 10000:
            with open(txt_path, 'w', encoding='utf-8') as f:
                f.write(best_text)
            print(f"  Saved to {txt_path} ({len(best_text)} chars)")
        else:
            print(f"  Not enough text to save (need >10000 chars)")

if __name__ == '__main__':
    main()
