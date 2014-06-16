<html>
<body>
<h2>Supernova Events Api</h2>
Welcome. This is a proxy for MixPanel APIs, you can find more by visiting <a href="http://mixpanel.com/docs/">http://mixpanel.com/docs/</a>
<p>
A fast events <a href="./track">tracking endpoint</a>, compatible with MixPanel endpoint, in order to proxy and enrich events when necessary. It 
will expose an API compatible with the MixPanel one, 
see <a href='https://mixpanel.com/docs/api-documentation/http-specification-insert-data'>here</a>
<br/>Please note that the "test" parameter is NOT supported
</p>

<p>
It will also have the capability to enrich events based on an internal rules. In order to collect the necessary information from the platform it will use the session cookie, if available, or a signed call the the platform based on the user email contained in the event
</p>

</p>
Initially the microservice will forward all the events again to MixPanel and later it will be changed so that the Daisy project will receive them, providing a seamless integration
</p>
</body>
</html>
