import React from 'react';
import Layout from '../components/Layout';

function ErrorPage() {
    return(
        <Layout>
            <div className='row justify-content-center'>
                <div className='col text-center'>
                    <p className='text-danger'>You are not authorize to access page</p>
                </div>
            </div>
        </Layout>
    );
}

export default ErrorPage;