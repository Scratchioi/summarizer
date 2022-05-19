from django import forms

class UploadFileForm(forms.Form):
    ext = forms.CharField(max_length=50)
    file = forms.FileField()