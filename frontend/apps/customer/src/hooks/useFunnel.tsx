import { type ReactElement, type ReactNode, useState, useEffect } from 'react';

export interface StepProps {
  name: string;
  children: ReactNode;
}

export interface FunnelProps {
  children: Array<ReactElement<StepProps>>;
}

export const useFunnel = <T extends readonly string[]>(
  steps: T,
  defaultStep: T[number],
) => {
  const [step, setStep] = useState<T[number]>(defaultStep);

  useEffect(() => {
    window.scrollTo({
      top: 0,
      left: 0,
    });
  }, [step]);
  const Step = (props: StepProps): ReactElement => {
    return <>{props.children}</>;
  };

  const Funnel = ({ children }: FunnelProps) => {
    const targetStep = children.find(
      childStep => childStep.props.name === step,
    );
    return <>{targetStep}</>;
  };

  const nextClickHandler = (nextStep: T[number]) => {
    setStep(nextStep);
  };

  const prevClickHandler = (prevStep: T[number]) => {
    setStep(prevStep);
  };

  return {
    Funnel,
    Step,
    currentStep: step,
    nextClickHandler,
    prevClickHandler,
  } as const;
};
