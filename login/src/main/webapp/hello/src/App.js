import React, { useState, useEffect } from 'react';
import './App.css';

function App() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [roles, setUserRoles] = useState([]);

    useEffect(() => {
        // This effect will run whenever userRoles change
        console.log('User roles have changed:', roles);
    }, [roles]); // Only run the effect when userRoles change

    const handleSubmit = async (e) => {
        e.preventDefault();
try {
 const response = await fetch('http://localhost:8882/login/api/V1/login', {
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
        throw new Error(`HTTP error! Status: ${response.status}`);
    }

    // Parse the JSON response
    const data = await response.json();

    // Extract user roles from the response
    const roles2 = data.roles;

    // Continue with your logic
            // Handle roles accordingly (e.g., redirect based on role)
            if (roles2.includes('DEVELOPER')) {
                // Redirect to developer dashboard
            } else if (roles2.includes('SUPERUSER')) {
                // Redirect to superuser dashboard
            } else if (roles2.includes('USER')) {
                // Redirect to user dashboard
            } else {
                // Handle unknown roles or other logic
            }
          // Set user roles in state only if they have changed
            //if (roles2.length >= 1 && JSON.stringify(roles) != JSON.stringify(roles2)) {
                setUserRoles(roles2);
            //}
 } catch (error) {
    // Handle fetch or other errors
    console.error('Login failed', error);
}

    };

    return (
    <div className="App">
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
            </form>
            <br />
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

            <br />
            <a href="http://localhost:8882/login/api/V1/logout">Logout</a>
        </div>
    );
};

export default App;