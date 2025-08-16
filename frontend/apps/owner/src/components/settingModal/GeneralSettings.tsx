import NotificationSetup from '../NotificationSetup';
import NotificationSettings from '../pages/settings/NotificationSettings';

const GeneralSettings = () => {
  return (
    <div className="space-y-6">
      <NotificationSettings />
      <NotificationSetup />
    </div>
  );
};

export default GeneralSettings;
