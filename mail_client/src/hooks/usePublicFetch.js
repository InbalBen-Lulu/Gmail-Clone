import { getApiUrl } from '../utils/authUtils';

const usePublicFetch = () => {
    return async (path, options = {}) => {
        const url = getApiUrl(path);

        const headers = {
            'Content-Type': 'application/json',
            ...options.headers,
        };

        const response = await fetch(url, {
            ...options,
            headers: {
                ...headers,
                'Cache-Control': 'no-store'
            },
            credentials: 'include'
        });

        return response;
    };
};

export default usePublicFetch;
