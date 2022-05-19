function myFunction() {
  
  

  let j=document.getElementById("exampleFormControlTextarea1").value;
  const data = { text: j };

fetch('http://127.0.0.1:8000/convert/', {
  method: 'POST', // or 'PUT'
  headers: {
    'Content-Type': 'application/json',
  },
  body: JSON.stringify(data),
})
.then(response => response.json())
.then(data => {
  let value1=data.text
  console.log(value1);
})
.catch((error) => {
  console.error('Error:', error);
});
  
document.getElementById('exampleFormControlTextarea2').innerHTML=JSON.stringify(data.text)


// .catch(error => console.log('error', error));

// data = fetch(url).then((res)=> res.json())
}



function reload(){
  location.reload()
}

function myFunction2(){
let t= myFunction()
console.log(t);

}
