

var bc = new BroadcastChannel('token_user_channel');

// var url = window.location.href;
// console.log ("url: " + url);

function sendcallback (data)
{
  var msg = JSON.stringify(data);
  console.log ("sending back: ");
  console.log (msg);
  bc.postMessage(msg); 
}

// window.location.hash = '';