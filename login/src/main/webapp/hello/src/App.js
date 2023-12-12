import React, { useState, useEffect } from 'react';
import './App.css';

function App() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [roles, setUserRoles] = useState([]);
    const [roles3, setRole3] = useState('');
    const [roles4, setRole4] = useState('');
    const [roles5, setRole5] = useState('');

    useEffect(() => {
        // This effect will run whenever userRoles change
        console.log('User roles have changed:', roles);
    }, [roles]); // Only run the effect when userRoles change
const getErrorMessage = (status) => {
  switch (status) {
    case 400:
      return "Bad Request - The server could not understand the request.";
    case 401:
      return "Unauthorized - Authentication failed or user lacks necessary permissions.";
   	case 302:
      return "Found - but not successful.";
    case 403:
      return "Forbidden - The server understood the request, but refuses to authorize it.";
    case 404:
      return "Not Found - The requested resource could not be found on the server.";
    case 500:
      return "Internal Server Error - The server encountered a situation it doesn't know how to handle.";
    default:
      return `HTTP error! Status - ${status}`;
  }
};

    
    const handleSubmit = async (e) => {
        e.preventDefault();
try {
 const response = await fetch('http://localhost:8882/login/api/V1/loginAttempt', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            username,
            password,
        }),
    });

    if (!response.ok) {
        // Handle non-successful response, e.g., throw an error or handle accordingly
      throw new Error(getErrorMessage(response.status));
    } else {
		console.error("Login Succeded!");
		alert("Login Succeded!");
	}

    // Parse the JSON response
    const data = await response.json();

    // Extract user roles from the response
    const roles2 = data.roles;

    setUserRoles(roles2);
 } catch (error) {
    // Handle fetch or other errors
    console.error('Login failed', error);
    alert("Login Failed: " + error.message);
}

    };
    const registerSubmit = async (e) => {
        e.preventDefault();
    try {
   const inputRoles2 = [roles3,roles4,roles5];
	console.log("inputRoles2 "+inputRoles2);
 const response = await fetch('http://localhost:8882/login/api/V1/register', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            username,
            password,
            roles: inputRoles2
        }),
    });
	console.log("response.status ==" + response.status);

    if (!response.ok || response.status === 302) {
        // Handle non-successful response, e.g., throw an error or handle accordingly
      throw new Error(getErrorMessage(response.status));
    }

    // Parse the JSON response
    const data = await response.json();
	console.error("Registration Succeded!");
	alert("Registration Succeded!");
    setUserRoles(inputRoles2);

 
 } catch (error) {
    // Handle fetch or other errors
    console.error('Registration failed', error);
    alert("Registration Failed: " + error.message);

}

    };

    return (
    <div className="App">
      <div style={{ textAlign: 'center' }}>
        <img src="IEEEHeader.png" alt="IEEEHeader.png" style={{ display: 'block', margin: 'auto' }} />
      </div>
    	<br/>
          <div className="form-container">

            <h1>Login</h1>
            <form onSubmit={handleSubmit}>
                <label>
                    Username:
                    <input type="text" value={username} onChange={(e) => setUsername(e.target.value)} />
                </label>
                <br />
                <label>
                    Password:
                    <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} />
                </label>
                <br />
                <button type="submit">Login</button>
                <a href="http://localhost:8882/login/api/V1/logout" style={{ display: 'inline-block', padding: '5px', backgroundColor: 'rgb(255, 100, 100)', color: 'black', textDecoration: 'none', textAlign: 'center', borderRadius: '10px',border: '2px solid black', margin: '5px'}}>
         		Logout
        		</a>

            </form>
            </div>
            <br />
            <div className="form-container">

            {roles.length > 0 && (
                <div>
                    <h2>User Roles:</h2>
                    <ul>
                        {roles.map((role, i) => (
                            <li key={i}>{role}</li>
                        ))}
                    </ul>
                </div>
               )}
			</div>
            <br />
            <div className="form-container">

               <h1>Register</h1>
            <form onSubmit={registerSubmit}>
                <label>
                    Username:
                    <input type="text" value={username} onChange={(e) => setUsername(e.target.value)} />
                </label>
                <br />
                <label>
                    Password:
                    <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} />
                </label>
                <br />
                <label>
                    Roles:
                    <input type="text" value={roles3} onChange={(e) =>setRole3(e.target.value)}/>
                    <input type="text" value={roles4} onChange={(e) =>setRole4(e.target.value)}/>
                    <input type="text" value={roles5} onChange={(e) =>setRole5(e.target.value)}/>
                </label>
                <br />
                <button type="submit">Register</button>
            </form>
            </div>
            <br />
      <div style={{ textAlign: 'center' }}>
        <img src="IEEEFooter.png" alt="IEEEFooter.png" style={{ display: 'block', margin: 'auto' }} />
      </div>

        </div>
    );
};

export default App;