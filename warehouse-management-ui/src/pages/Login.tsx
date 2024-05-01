import React,{ FormEvent, useEffect, useState } from 'react'
import { useNavigate } from "react-router-dom"
import { JWTRequest, AuthControllerApi, JWTResponse, ExceptionResponse } from '../services/generated/openapi'
import Layout from "../components/Layout"
import { error } from 'console'
  
function Login() {
    const navigate = useNavigate();
    const [username,setUsername] = useState('');
    const [password,setPassword] = useState('');
    const [validationError, setValidationError] = useState('');
    const [isSubmitting, setIsSubmitting] = useState(false);

    useEffect(()=>{
        const stringUser = sessionStorage.getItem('user');
        if(stringUser){
            navigate("/dashboard");
        }
    },[]);

    const loginAction = (e:FormEvent) => {
        setValidationError('');
        e.preventDefault();
        setIsSubmitting(true);
        
        let payload:JWTRequest = {
            username: username,
            password: password
        }

        const authService:AuthControllerApi = new AuthControllerApi();

        authService.login({jWTRequest:payload})
        .then( (response) => {
                setIsSubmitting(false);
                let user:JWTResponse = response.data;
                sessionStorage.setItem('user',JSON.stringify(user));  
                navigate('/dashboard');
        }).catch((error) => {
            setIsSubmitting(false);
            if(error.response && error.response.data ){
                setValidationError(error.response.data.message)
            }
            
        }

        );  

    }

    return(
        <Layout>
            <div className='row justify-content-md-center mt-5'>
                <div className='col-12 text-center'> <h4>Warehouse Software</h4></div>
            </div>
            <div className='row justify-content-md-center mt-5'>
                <div className='col-4'>
                    <div className='card'>
                        <div className='card-body'>
                            <div className='text-center'><h5 className='card-title mb-4'> Sign In</h5></div>
                            <form onSubmit={(e)=>{loginAction(e)}}>
                                {
                                    validationError && validationError.length>0 &&
                                    <p className='text-center'>
                                        <small className='text-danger'>{validationError + '. Incorrect username or password'}</small>
                                    </p>
                                }
                                <div className='mb-3'>
                                    <label htmlFor='username' className='form-label'>Username</label>
                                    <input id='username'
                                            name='username'
                                            type='text'
                                            className='form-control'
                                            value={username}
                                            onChange={(e)=>{setUsername(e.target.value)}}
                                    />
                                </div>
                                <div className='mb-3'>
                                    <label htmlFor='password' className='form-label'>Password</label>
                                    <input id='password'
                                            name='password'
                                            type='password'
                                            className='form-control'
                                            value={password}
                                            onChange={(e)=>{setPassword(e.target.value)}}
                                    />
                                </div>
                                <div className='d-grid gap-2'>
                                    <button
                                        disabled={isSubmitting}
                                        type='submit'
                                        className='btn btn-primary btn-block'
                                        >Login</button>
                                </div>
                            </form>
                        </div>

                    </div>

                </div>
            </div>

        </Layout>
    );
}

export default Login