import NotificationSetup from '@/components/NotificationSetup';

const Settings = () => {
  return (
    <div>
      <h1 className="mb-6 text-2xl font-bold">설정</h1>
      <div className="space-y-6">
        <NotificationSetup />
      </div>
    </div>
  );
};

export default Settings;
