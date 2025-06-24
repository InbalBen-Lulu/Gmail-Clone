import { createContext, useContext, useState, useCallback } from 'react';
import { useLabelService } from '../services/useLabelService';

const LabelContext = createContext();

/**
 * Provider that supplies all labels + refresh function.
 */
export const LabelProvider = ({ children }) => {
    const [labels, setLabels] = useState([]);
    const { fetchLabels } = useLabelService();

    const refreshLabels = useCallback(async () => {
        try {
            const fetched = await fetchLabels();
            setLabels(fetched);
        } catch (err) {
            console.error('Failed to fetch labels:', err);
        }
    }, [fetchLabels]);

    return (
        <LabelContext.Provider value={{ labels, refreshLabels }}>
            {children}
        </LabelContext.Provider>
    );
};

/**
 * Returns the label context.
 * Must be used within a LabelProvider.
 */
export const useLabels = () => {
    const context = useContext(LabelContext);
    if (!context) throw new Error('useLabels must be used within a LabelProvider');
    return context;
};
