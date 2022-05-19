from django.shortcuts import render
from rest_framework.response import Response
from rest_framework import viewsets,status,generics,views,filters
import time
import nltk
from nltk.corpus import stopwords
from nltk.tokenize import word_tokenize, sent_tokenize
from django.http import HttpResponse
from django.views.decorators.csrf import csrf_exempt


from .forms import UploadFileForm
def summarize(text):
	# Tokenizing the text
	stopWords = set(stopwords.words("english"))
	words = word_tokenize(text)

	# Creating a frequency table to keep the
	# score of each word

	freqTable = dict()
	for word in words:
		word = word.lower()
		if word in stopWords:
			continue
		if word in freqTable:
			freqTable[word] += 1
		else:
			freqTable[word] = 1

	# Creating a dictionary to keep the score
	# of each sentence
	sentences = sent_tokenize(text)
	sentenceValue = dict()

	for sentence in sentences:
		for word, freq in freqTable.items():
			if word in sentence.lower():
				if sentence in sentenceValue:
					sentenceValue[sentence] += freq
				else:
					sentenceValue[sentence] = freq



	sumValues = 0
	for sentence in sentenceValue:
		sumValues += sentenceValue[sentence]

	# Average value of a sentence from the original text

	average = int(sumValues / len(sentenceValue))

	# Storing sentences into our summary.
	summary = ''
	for sentence in sentences:
		if (sentence in sentenceValue) and (sentenceValue[sentence] > (1.2 * average)):
			summary += " " + sentence
	print(summary)
	# print(len(summary))
	return summary

class converting(views.APIView):
    def post(self, request):
        try:
            print(request.data)
            text = request.data["text"]
            summary = summarize(text)
            # print(summary)
            return Response({"text": summary}, status=status.HTTP_200_OK)
        except Exception as e:
            return Response({"error": str(e)}, status=500)



def handle_uploaded_file(title,f,ext):

	from google.cloud import storage, speech
	import os
	from subprocess import PIPE, run

	filepath = f'/home/ubuntu/minor-proj/summarizer/backend/summarizer/files/{title}.{ext}'
	with open(filepath, 'wb+') as destination:
		for chunk in f.chunks():
			destination.write(chunk)
	
	flac_file = '/home/ubuntu/minor-proj/summarizer/backend/summarizer/files/{}.flac'.format(title)
	command = "ffmpeg -i {} {} -ar 16000".format(filepath,flac_file)
	output = run(command, stdout=PIPE, stderr=PIPE, universal_newlines=True, shell=True)
	output = output.__str__()
	sample_rate = int(output.split(": Audio: flac, ")[1].split(" ")[0])

	if "stereo" in output:
		audio_channels = 2
	else:
		audio_channels = 1
	
	storage_client = storage.Client.from_service_account_json(r"/home/ubuntu/minor-proj/rising-cable-348608-a1a7404667d8.json")
	
	bucket_name = "summarize-bucket"
	blob_name = title + ".flac"
	
	bucket= storage_client.bucket(bucket_name)
	blob = bucket.blob(blob_name)
	blob.upload_from_filename(flac_file)
	uri = "gs://{}/{}".format(bucket_name, blob_name)

	client = speech.SpeechClient.from_service_account_json(r"/home/ubuntu/minor-proj/rising-cable-348608-a1a7404667d8.json")
	
	audio = speech.RecognitionAudio(uri=uri)
	config = speech.RecognitionConfig(
        encoding=speech.RecognitionConfig.AudioEncoding.FLAC,
        audio_channel_count = audio_channels,
        sample_rate_hertz=sample_rate,
        language_code="en-US",
    )
	
	operation = client.long_running_recognize(config=config, audio=audio)
	response = operation.result(timeout=100)

    # Each result is for a consecutive portion of the audio. Iterate through
    # them to get the transcripts for the entire audio file.
	output = ""
	for result in response.results:
        # The first alternative is the most likely one for this portion.
		output += result.alternatives[0].transcript
		print(u"Transcript: {}".format(result.alternatives[0].transcript))
	s = summarize(output)
	if len(s) == 0


# Imaginary function to handle an uploaded file.
@csrf_exempt
def upload_file(request):
	if request.method == 'POST':
		print("in")
		form = UploadFileForm(request.POST, request.FILES)
		print(form)
		if form.is_valid():
			print("valid")
			output = handle_uploaded_file(str(time.time()),request.FILES['file'],form.cleaned_data['ext'])
			return HttpResponse(output)
	return HttpResponse("nhi hua")


        


